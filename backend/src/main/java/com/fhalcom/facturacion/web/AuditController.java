package com.fhalcom.facturacion.web;
import org.springframework.web.bind.annotation.*;
import com.fhalcom.facturacion.audit.AuditService;
import com.fhalcom.facturacion.audit.AuditRepo;
import java.util.Map;

@RestController @RequestMapping("/audit")
public class AuditController {
  private final AuditService svc; private final AuditRepo repo;
  public AuditController(AuditService s, AuditRepo r){ this.svc=s; this.repo=r; }

  @GetMapping("/verify")
  public Map<String,Object> verify(@RequestParam String tenant){
    return java.util.Map.of("tenant", tenant, "valid", svc.verify(tenant));
  }
}
