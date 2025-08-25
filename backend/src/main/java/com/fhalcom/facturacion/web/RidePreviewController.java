package com.fhalcom.facturacion.web;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import com.fhalcom.facturacion.ride.RideService;

@RestController @RequestMapping("/ride/preview")
public class RidePreviewController {
  @Autowired RideService ride;

  @com.facturacion.validation.ValidateXsd
  @PostMapping(value="/{docType}", consumes=MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<byte[]> preview(@PathVariable String docType, @RequestParam String tenant, @RequestParam(defaultValue="false") boolean includeAuth, @RequestBody(required=false) byte[] xml) throws Exception{
    if(xml==null){
      String res = "/ride/samples/"+docType+".xml";
      var in = getClass().getResourceAsStream(res);
      if(in==null) throw new RuntimeException("Sample not found: "+res);
      xml = in.readAllBytes();
    }
    var dbf = DocumentBuilderFactory.newInstance(); dbf.setNamespaceAware(true);
    Document d = dbf.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(xml));
    String numAut = includeAuth? "0000000000" : null;
    String fecAut = includeAuth? java.time.OffsetDateTime.now().toString() : null;
    byte[] pdf = ride.render(d, tenant, docType, "PREVIEW", numAut, fecAut);
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdf);
  }
}
