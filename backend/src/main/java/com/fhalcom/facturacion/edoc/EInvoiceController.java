
package com.fhalcom.facturacion.edoc;
import com.fhalcom.facturacion.security.PermEvaluator;
import com.fhalcom.facturacion.tenancy.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController @RequestMapping("/einvoice")
public class EInvoiceController {
  private final EInvoiceService svc;
  public EInvoiceController(EInvoiceService svc){ this.svc = svc; }

  @PreAuthorize("@perm.hasPermission(authentication, null, 'EINVOICE.WRITE')")
  @PostMapping
  public Map<String,Object> create(@RequestBody Map<String,Object> body){
    String ruc = (String) body.get("ruc");
    String sec = (String) body.get("secuencial");
    String claveAcceso = (String) body.get("claveAcceso");
    var d = svc.create(TenantContext.getTenant(), ruc, sec, claveAcceso);
    return Map.of("id", d.id);
  }

  @PreAuthorize("@perm.hasPermission(authentication, null, 'EINVOICE.WRITE')")
  @PostMapping("/{id}/send")
  public ResponseEntity<?> send(@PathVariable UUID id){ var d = svc.send(id); return ResponseEntity.ok(Map.of("estado", d.estado)); }

  @PreAuthorize("@perm.hasPermission(authentication, null, 'EINVOICE.WRITE')")
  @PostMapping("/{id}/authorize")
  public ResponseEntity<?> auth(@PathVariable UUID id){ var d = svc.authorize(id); return ResponseEntity.ok(Map.of("estado", d.estado, "num", d.sri_num_autorizacion)); }
}
