
package com.fhalcom.facturacion.branding;
import org.springframework.web.bind.annotation.*; import org.springframework.http.*;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
@RestController @RequestMapping("/branding") @Tag(name="Branding")
public class BrandingController {
  private final BrandingRepository repo;
  public BrandingController(BrandingRepository repo){ this.repo=repo; }
  private String tenant(HttpHeaders h){ return h.getFirst("X-Tenant-Id"); }
  @Operation(summary="Obtener logo del tenant")
  @GetMapping(produces=MediaType.IMAGE_PNG_VALUE)
  @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('TENANT_ADMIN','BRANDING_ADMIN','BRANDING_VIEWER','RIDE_RENDER')")
  public ResponseEntity<byte[]> get(@RequestHeader HttpHeaders headers){
    String t = tenant(headers); return repo.findByTenantId(t).map(b->ResponseEntity.ok().contentType(MediaType.valueOf(b.getContentType())).body(b.getLogo())).orElse(ResponseEntity.notFound().build());
  }
  @Operation(summary="Guardar logo del tenant (PNG)")
  @PutMapping(consumes=MediaType.APPLICATION_OCTET_STREAM_VALUE)
  @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('TENANT_ADMIN','BRANDING_ADMIN')")
  public ResponseEntity<Void> put(@RequestHeader HttpHeaders headers, @RequestBody byte[] logo){
    String t = tenant(headers); Branding b = repo.findByTenantId(t).orElseGet(Branding::new);
    b.setTenantId(t); b.setLogo(logo); b.setContentType(MediaType.IMAGE_PNG_VALUE); b.setUpdatedAt(java.time.OffsetDateTime.now());
    repo.save(b); return ResponseEntity.noContent().build();
  }
}
