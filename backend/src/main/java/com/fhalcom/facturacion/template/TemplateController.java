
package com.fhalcom.facturacion.template;
import org.springframework.web.bind.annotation.*; import org.springframework.http.*;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
@RestController @RequestMapping("/templates") @Tag(name="JRXML Templates")
public class TemplateController {
  private final TemplateRepository repo; private final TemplateService service;
  public TemplateController(TemplateRepository repo, TemplateService service){ this.repo=repo; this.service=service; }
  private String tenant(org.springframework.http.HttpHeaders h){ return h.getFirst("X-Tenant-Id"); }
  @Operation(summary="Obtener JRXML por tipo y tenant")
  @GetMapping(value="/{docType}", produces=MediaType.TEXT_PLAIN_VALUE)
  @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('TENANT_ADMIN','TEMPLATE_ADMIN','TEMPLATE_VIEWER','RIDE_RENDER')")
  public ResponseEntity<String> get(@RequestHeader HttpHeaders headers, @PathVariable String docType){
    String t = tenant(headers); String jr = service.getJrxml(t, docType);
    if(jr==null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    return ResponseEntity.ok(jr);
  }
  @Operation(summary="Guardar JRXML por tipo y tenant")
  @PutMapping(value="/{docType}", consumes=MediaType.TEXT_PLAIN_VALUE)
  @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('TENANT_ADMIN','TEMPLATE_ADMIN')")
  public ResponseEntity<Void> put(@RequestHeader HttpHeaders headers, @PathVariable String docType, @RequestBody String jrxml){
    String t = tenant(headers); if(t==null || t.isBlank()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    service.save(t, docType, jrxml); return ResponseEntity.noContent().build();
  }
}
