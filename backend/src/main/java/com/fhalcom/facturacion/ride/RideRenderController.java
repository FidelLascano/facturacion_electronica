
package com.fhalcom.facturacion.ride;
import com.fhalcom.facturacion.template.TemplateService;
import net.sf.jasperreports.engine.*; import org.springframework.web.bind.annotation.*; import org.springframework.http.*;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.ByteArrayInputStream; import java.util.HashMap;
@RestController @RequestMapping("/ride") @Tag(name="RIDE Render")
public class RideRenderController {
  private final TemplateService templates;
  public RideRenderController(TemplateService t){ this.templates=t; }
  private String tenant(HttpHeaders h){ return h.getFirst("X-Tenant-Id"); }
  @Operation(summary="Render PDF del RIDE a partir de JRXML en DB y XML opcional")
  @PostMapping(value="/render.pdf/{docType}", consumes={"application/xml","text/xml","application/octet-stream"})
  @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('TENANT_ADMIN','RIDE_RENDER','TEMPLATE_ADMIN')")
  public ResponseEntity<byte[]> render(@RequestHeader HttpHeaders headers, @PathVariable String docType, @RequestBody(required=false) byte[] xml){
    try{
      String jrxml = templates.getJrxml(tenant(headers), docType);
      if(jrxml==null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      JasperReport jr = JasperCompileManager.compileReport(new java.io.ByteArrayInputStream(jrxml.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
      java.util.Map<String,Object> params = new HashMap<>();
      params.put("NUM_AUT", headers.getFirst("X-Num-Aut"));
      params.put("FEC_AUT", headers.getFirst("X-Fec-Aut"));
      params.put("QR_TEXT", headers.getFirst("X-QR-Text"));
      JasperPrint jp = JasperFillManager.fillReport(jr, params, new net.sf.jasperreports.engine.JREmptyDataSource(1));
      byte[] pdf = JasperExportManager.exportReportToPdf(jp);
      return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdf);
    }catch(Exception e){ throw new RuntimeException(e); }
  }
}
