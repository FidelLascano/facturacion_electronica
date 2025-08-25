
package com.fhalcom.facturacion.web;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import com.fhalcom.facturacion.repo.EDocRepository;
import com.fhalcom.facturacion.domain.EDoc;
import com.fhalcom.facturacion.service.SriClient;
import com.fhalcom.facturacion.service.RideService;
import com.fhalcom.facturacion.storage.S3Storage;
import com.fhalcom.facturacion.webhooks.WebhookService;

@RestController @RequestMapping("/edoc")
public class EDocFlowController {
  private final EDocRepository repo; private final SriClient sri; private final S3Storage s3; private final WebhookService hooks;
  public EDocFlowController(EDocRepository r, SriClient s, S3Storage s3, WebhookService h){ this.repo=r; this.sri=s; this.s3=s3; this.hooks=h; }

  @PostMapping("/{id}/send")
  public EDoc send(@PathVariable Long id){
    EDoc e = repo.findById(id).orElseThrow();
    byte[] xml = s3.getBytes(e.xmlPath);
    String b64 = java.util.Base64.getEncoder().encodeToString(xml);
    sri.sendRecepcion(b64);
    e.estado = "RECEIVED"; return repo.save(e);
  }

  @PostMapping("/{id}/authorize")
  public EDoc authorize(@PathVariable Long id){
    EDoc e = repo.findById(id).orElseThrow();
    String num = sri.requestAutorizacion(e.claveAcceso);
    if(num!=null){
      e.estado = "AUTHORIZED"; e.numeroAutorizacion = num;
      byte[] ride;
var params = java.util.Map.of("razonSocial","ACME SA","secuencial","000000001","total","0.00");
var items = java.util.List.of(java.util.Map.of("item","Item demo","qty","1","price","0.00"));
switch(e.tipo){
  case "FACTURA" -> ride = RideService.factura(params, items);
  case "NOTA_CREDITO" -> ride = RideService.notaCredito(params, items);
  case "NOTA_DEBITO" -> ride = RideService.notaDebito(params, items);
  case "GUIA_REMISION" -> ride = RideService.guia(params, items);
  case "RETENCION" -> ride = RideService.retencion(params, items);
  case "LIQUIDACION" -> ride = RideService.liquidacion(params, items);
  default -> ride = RideService.factura(params, items);
}
      String key = "ride/%s/%s.pdf".formatted(e.tipo.toLowerCase(), e.claveAcceso);
      s3.putBytes(key, ride, "application/pdf"); e.pdfPath = key; repo.save(e);
      hooks.publish("edoc.authorized", java.util.Map.of("id", e.id, "tipo", e.tipo, "claveAcceso", e.claveAcceso, "numeroAutorizacion", num));
    } else {
      e.estado = "REJECTED"; repo.save(e);
    }
    return e;
  }

  @GetMapping("/{id}/download")
  public ResponseEntity<byte[]> download(@PathVariable Long id, @RequestParam String type){
    EDoc e = repo.findById(id).orElseThrow();
    String key = "xml".equalsIgnoreCase(type)? e.xmlPath : e.pdfPath;
    String ct = "xml".equalsIgnoreCase(type)? "application/xml":"application/pdf";
    byte[] bytes = s3.getBytes(key);
    return ResponseEntity.ok().contentType(MediaType.parseMediaType(ct)).body(bytes);
  }
}
