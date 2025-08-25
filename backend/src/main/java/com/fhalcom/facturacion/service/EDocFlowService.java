
package com.fhalcom.facturacion.service;
import com.fhalcom.facturacion.domain.EDoc;
import com.fhalcom.facturacion.domain.Company;
import com.fhalcom.facturacion.repo.EDocRepository;
import com.fhalcom.facturacion.repo.CompanyRepository;
import com.fhalcom.facturacion.storage.S3Storage;
import com.fhalcom.facturacion.kms.KmsService;
import com.fhalcom.facturacion.sign.XmlSigner;
import com.fhalcom.facturacion.sign.XadesSigner;
import com.fhalcom.facturacion.webhooks.WebhookService;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

@Service
public class EDocFlowService {
  private final EDocRepository repo; private final CompanyRepository companies; private final S3Storage s3;
  private final KmsService kms; private final WebhookService hooks;
  public EDocFlowService(EDocRepository r, CompanyRepository c, S3Storage s3, KmsService kms, WebhookService h){
    this.repo=r; this.companies=c; this.s3=s3; this.kms=kms; this.hooks=h;
  }

  public EDoc signAndStore(EDoc e, Document doc, String companyRuc){
    try{
      Company c = companies.findByRuc(companyRuc).orElseThrow();
      String key = c.certKey; if(key==null) throw new RuntimeException("Company certificate not uploaded");
      // Download cert to temp
      byte[] p12 = s3.getBytes(key);
      Path tmp = Files.createTempFile("cert",".p12"); Files.writeString(tmp, new String(p12)); // here we persist the bytes
      String mode = System.getenv().getOrDefault("APP_SIGNATURE_MODE","xades");
      String pin = kms.pinForCompany(c.id.toString());
      Document signed = "xades".equalsIgnoreCase(mode) ? XadesSigner.sign(doc, tmp.toString(), pin) : XmlSigner.enveloped(doc, null, null);
      // Serialize
      var sw = new StringWriter();
      TransformerFactory.newInstance().newTransformer().transform(new DOMSource(signed), new StreamResult(sw));
      byte[] xml = sw.toString().getBytes();
      String s3key = "docs/%s/%s.xml".formatted(e.tipo.toLowerCase(), e.claveAcceso);
      s3.putBytes(s3key, xml, "application/xml");
      e.xmlPath = s3key; e.estado = "SIGNED";
      return repo.save(e);
    }catch(Exception ex){ throw new RuntimeException(ex); }
  }

  public void afterAuthorized(EDoc e){
    hooks.publish("edoc.authorized", java.util.Map.of("id", e.id, "tipo", e.tipo, "claveAcceso", e.claveAcceso, "numAut", e.numeroAutorizacion));
  }
}
