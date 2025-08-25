
package com.fhalcom.facturacion.signature;
import org.springframework.web.bind.annotation.*; import org.springframework.http.ResponseEntity;
@RestController @RequestMapping("/signature")
public class SignatureReloadController {
  private final SignatureHotReloader r; public SignatureReloadController(SignatureHotReloader r){ this.r=r; }
  @PostMapping("/reload") @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('TENANT_ADMIN','RIDE_ADMIN')")
  public ResponseEntity<Void> reload(){ r.checkAndReloadNow(); return ResponseEntity.noContent().build(); }
}
