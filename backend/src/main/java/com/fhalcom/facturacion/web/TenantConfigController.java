package com.fhalcom.facturacion.web;
import org.springframework.web.bind.annotation.*;
import com.fhalcom.facturacion.tenant.*;
import java.util.Map;

@RestController @RequestMapping("/tenant-config")
public class TenantConfigController {
  private final TenantConfigRepo repo;
  public TenantConfigController(TenantConfigRepo r){ this.repo=r; }
  @GetMapping public Map<String,Object> get(@RequestHeader("X-Tenant-Id") String tenant){
    var c = repo.findByTenantId(tenant).orElseGet(()->{ var x=new TenantConfig(); x.tenantId=tenant; x.json="{}"; return repo.save(x); });
    return Map.of("tenantId", tenant, "config", c.json);
  }
  @PostMapping public Map<String,Object> set(@RequestHeader("X-Tenant-Id") String tenant, @RequestBody String json){
    var c = repo.findByTenantId(tenant).orElseGet(()->{ var x=new TenantConfig(); x.tenantId=tenant; x.json="{}"; return x; });
    c.json = json; repo.save(c);
    return Map.of("ok", true);
  }
}
