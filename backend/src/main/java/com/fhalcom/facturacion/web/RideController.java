package com.fhalcom.facturacion.web;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import com.fhalcom.facturacion.repo.EDocRepository;
import com.fhalcom.facturacion.domain.EDoc;
import com.fhalcom.facturacion.storage.S3Storage;
import com.fhalcom.facturacion.ride.RideService;
import com.fhalcom.facturacion.ride.RideQrBuilder;

@RestController @RequestMapping("/edocs")
public class RideController {
  @Autowired EDocRepository edocs; @Autowired S3Storage s3; @Autowired RideService ride; @Autowired RideQrBuilder qr;

  @GetMapping("/{id}/ride.pdf")
  public ResponseEntity<byte[]> ridePdf(@PathVariable Long id, @RequestHeader(value="X-Tenant-Id", required=false) String tenant) throws Exception {
    EDoc e = edocs.findById(id).orElseThrow();
    byte[] xml = s3.getBytes(e.xmlPath);
    var dbf = DocumentBuilderFactory.newInstance(); dbf.setNamespaceAware(true);
    Document d = dbf.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(xml));
    String docType = mapDocType(e.tipo);
    String qrText = qr.build(e);
    byte[] pdf = ride.render(d, tenant!=null?tenant:"default", docType, qrText, e.numeroAutorizacion, e.fechaAutorizacion!=null? e.fechaAutorizacion.toString():null);
    String pdfKey = "docs/"+docType+"/"+(e.claveAcceso!=null?e.claveAcceso:e.id)+".pdf";
    s3.putBytes(pdfKey, pdf, "application/pdf"); e.pdfPath = pdfKey; edocs.save(e);
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdf);
  }

  @com.facturacion.validation.ValidateXsd
  @PostMapping(value="/ride.pdf", consumes=MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<byte[]> rideFromXml(@RequestBody byte[] xml, @RequestHeader(value="X-Tenant-Id", required=false) String tenant, @RequestParam(defaultValue="factura") String docType) throws Exception{
    var dbf = DocumentBuilderFactory.newInstance(); dbf.setNamespaceAware(true);
    Document d = dbf.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(xml));
    byte[] pdf = ride.render(d, tenant!=null?tenant:"default", docType, null, null, null);
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdf);
  }

  private String mapDocType(String tipo){
    if(tipo==null) return "factura";
    switch(tipo.toUpperCase()){
      case "FACTURA": return "factura";
      case "NOTA_CREDITO": return "notaCredito";
      case "NOTA_DEBITO": return "notaDebito";
      case "GUIA_REMISION": return "guiaRemision";
      case "RETENCION": return "comprobanteRetencion";
      case "LIQUIDACION": return "liquidacionCompra";
      default: return "factura";
    }
  }
}
