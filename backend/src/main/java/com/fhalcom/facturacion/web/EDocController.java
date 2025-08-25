
package com.fhalcom.facturacion.web;
import com.fhalcom.facturacion.domain.EDoc;
import com.fhalcom.facturacion.repo.EDocRepository;
import com.fhalcom.facturacion.service.EDocBuilders;
import com.fhalcom.facturacion.xsd.XsdValidator;
import com.fhalcom.facturacion.xsd.XsdResolver;
import com.fhalcom.facturacion.service.RideService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Map;

record EDocCreate(String tipo, String version, String claveAcceso, String ruc){}

@RestController @RequestMapping("/edoc")
public class EDocController {
  private final EDocRepository repo;
  public EDocController(EDocRepository repo){ this.repo = repo; }

  @PostMapping("/create")
  public EDoc create(@RequestBody EDocCreate b){
    org.w3c.dom.Document doc;
    switch(b.tipo()){
      case "FACTURA" -> doc = EDocBuilders.factura(b.version(), b.claveAcceso(), b.ruc());
      case "NOTA_CREDITO" -> doc = EDocBuilders.notaCredito(b.version(), b.claveAcceso(), b.ruc());
      case "NOTA_DEBITO" -> doc = EDocBuilders.notaDebito(b.version(), b.claveAcceso(), b.ruc());
      case "GUIA_REMISION" -> doc = EDocBuilders.guia(b.version(), b.claveAcceso(), b.ruc());
      case "RETENCION" -> doc = EDocBuilders.retencion(b.version(), b.claveAcceso(), b.ruc());
      case "LIQUIDACION" -> doc = EDocBuilders.liquidacion(b.version(), b.claveAcceso(), b.ruc());
      default -> throw new IllegalArgumentException("tipo no soportado");
    }
    new XsdValidator().validate(doc, XsdResolver.pathFor(b.tipo(), b.version()));
    var sw = new StringWriter();
    try{ TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(sw)); }catch(Exception e){ throw new RuntimeException(e); }
    EDoc e = new EDoc();
    e.tipo = b.tipo(); e.version = b.version(); e.claveAcceso = b.claveAcceso(); e.estado="SIGNED";
    e.xmlPath = "inline:"+sw.toString();
    return repo.save(e);
  }

  @GetMapping("/{id}/xml")
  public ResponseEntity<String> xml(@PathVariable Long id){
    EDoc e = repo.findById(id).orElseThrow();
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(e.xmlPath.startsWith("inline:")?e.xmlPath.substring(7):e.xmlPath);
  }

  @GetMapping("/{id}/ride")
  public ResponseEntity<byte[]> ride(@PathVariable Long id){
    EDoc e = repo.findById(id).orElseThrow();
    byte[] pdf = RideService.renderFactura(Map.of("razonSocial","ACME SA","secuencial","000000001","total","0.00"));
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdf);
  }
}
