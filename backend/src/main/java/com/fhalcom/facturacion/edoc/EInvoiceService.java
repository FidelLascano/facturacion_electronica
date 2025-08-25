package com.fhalcom.facturacion.edoc;
import com.fhalcom.facturacion.company.Company;
import com.fhalcom.facturacion.company.CompanyRepo;
import com.fhalcom.facturacion.kms.KmsService;
import com.fhalcom.facturacion.limits.QuotaService;
import com.fhalcom.facturacion.ride.RideService;
import com.fhalcom.facturacion.xsd.XsdValidator;
import com.fhalcom.facturacion.xsd.XsdRegistry;
import com.fhalcom.facturacion.xsd.XsdVersionResolver;
import com.fhalcom.facturacion.xsd.PreValidationPipeline;
import com.fhalcom.facturacion.sign.XmlSigner;
import com.fhalcom.facturacion.sign.XadesSignerWrapper;
import com.fhalcom.facturacion.sri.SriGateway;
import com.fhalcom.facturacion.storage.S3Service;
import com.fhalcom.facturacion.webhooks.WebhookService;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class EInvoiceService {
  private final XsdValidator xsd = new XsdValidator();
  private final XsdRegistry xreg = new XsdRegistry();
  private final XsdVersionResolver xres;
  private final EInvoiceRepo repo; private final S3Service s3; private final SriGateway sri;
  private final CompanyRepo companies; private final PreValidationPipeline preval; private final QuotaService quota; private final WebhookService wh;
  private final RideService ride; private final KmsService kms;
  private final String certKey;

  public EInvoiceService(EInvoiceRepo repo, S3Service s3, SriGateway sri, CompanyRepo companies, QuotaService quota,
                         WebhookService wh, RideService ride, KmsService kms, XsdVersionResolver xres,
                         @Value("${app.cert.objectKey:certs/demo.p12}") String certKey){
    this.repo=repo; this.s3=s3; this.sri=sri; this.companies=companies; this.preval=preval; this.quota=quota; this.wh=wh; this.ride=ride; this.kms=kms; this.xres=xres; this.certKey = certKey;
  }

  public EInvoice create(String tenantKey, String ruc, String secuencial, String claveAcceso){
    // Quota check
    if(!quota.consume(tenantKey)){ throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.TOO_MANY_REQUESTS, "Daily quota reached"); }
    // Resolve company (demo logic)
    Company c = companies.findByRuc(ruc).orElseGet(() -> {
      Company nc = new Company();
      nc.id = UUID.randomUUID(); nc.tenant_id = UUID.fromString("00000000-0000-0000-0000-000000000001"); nc.ruc = ruc; nc.razon_social="ACME SA";
      return companies.save(nc);
    });
    EInvoice d = new EInvoice();
    d.id = UUID.randomUUID();
    d.tenant_id = UUID.fromString("00000000-0000-0000-0000-000000000001");
    d.company_id = c.id;
    d.ruc = ruc; d.secuencial = secuencial; d.clave_acceso = claveAcceso;
    d.estado = "CREATED";
    return repo.save(d);
  }

  public EInvoice send(UUID id){
    EInvoice d = repo.findById(id).orElseThrow();
    String unsigned = "<factura id='comprobante' version='1.1.0'><infoTributaria><claveAcceso>"+d.clave_acceso+"</claveAcceso><ruc>"+d.ruc+"</ruc></infoTributaria><infoFactura><secuencial>"+d.secuencial+"</secuencial><importeTotal>11.20</importeTotal></infoFactura></factura>";

    if(Boolean.parseBoolean(System.getenv().getOrDefault("APP_VALIDATE_XSD","false"))){
      try{
        // Build DOM
        var dbf = DocumentBuilderFactory.newInstance(); dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(unsigned.getBytes()));
        // Resolve XSD path using tenant policy
        String xsdPath = xres.resolvePath(d.tenant_id.toString(), "factura", "1.1.0");
        preval.validate(d.tenant_id.toString(), "FACTURA", "1.1.0", doc);
      }catch(Exception e){
        throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage());
      }
    }
catch(Exception e){ throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage()); }
    }
    String signed;
    if("xades".equalsIgnoreCase(System.getProperty("app.signature.mode", System.getenv().getOrDefault("APP_SIGNATURE_MODE","xmldsig")))){
      XadesSignerWrapper xs = new XadesSignerWrapper(s3.getClient(), s3.getBucket(), kms);
      signed = xs.sign(unsigned, d.company_id.toString(), certKey);
    } else {
      XmlSigner signer = new XmlSigner(s3.getClient(), s3.getBucket(), kms);
      signed = signer.signEnveloped(unsigned, d.company_id.toString(), certKey);
    }
    s3.put("docs/%s/signed.xml".formatted(d.id), signed);
    d.xml_s3_key = "docs/%s/signed.xml".formatted(d.id);
    boolean ok = sri.recepcion(signed);
    d.estado = ok ? "RECEIVED" : "REJECTED";
    return repo.save(d);
  }

  public EInvoice authorize(UUID id){
    EInvoice d = repo.findById(id).orElseThrow();
    String num = sri.autorizacion(d.clave_acceso);
    if(num!=null){
      d.estado = "AUTHORIZED"; d.sri_num_autorizacion = num;
      byte[] pdf = ride.generate("ACME SA", d.secuencial, "11.20");
      // store PDF
      try{
        s3.getClient().putObject(io.minio.PutObjectArgs.builder().bucket(s3.getBucket()).object("docs/%s/ride.pdf".formatted(d.id))
          .stream(new java.io.ByteArrayInputStream(pdf), pdf.length, -1).contentType("application/pdf").build());
        d.ride_s3_key = "docs/%s/ride.pdf".formatted(d.id);
      }catch(Exception ignored){}
      repo.save(d);
      // webhook
      wh.publish(d.tenant_id, "EINVOICE.AUTHORIZED", java.util.Map.of("id", d.id.toString(), "num", d.sri_num_autorizacion));
    }
    return repo.save(d);
  }
}
