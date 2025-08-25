package com.fhalcom.facturacion.xsd;
import org.springframework.stereotype.Component;
import com.fhalcom.facturacion.tenant.TenantConfigRepo;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class XsdVersionResolver {
  private final TenantConfigRepo tenants;
  private final XsdRegistry registry;
  private final ObjectMapper om = new ObjectMapper();

  public XsdVersionResolver(TenantConfigRepo t, XsdRegistry r){ this.tenants=t; this.registry=r; }

  public String resolvePath(String tenantId, String tipo, String requestedVersion){
    if(requestedVersion!=null && !requestedVersion.isBlank()){
      String p = registry.pathFor(tipo, requestedVersion);
      if(p!=null) return p;
    }
    // Tenant policy: tenant-config JSON may contain {"xsd":{"policy":"latest"|"pinned","pinned":{"factura":"1.1.0"...}}}
    try{
      var cfg = tenants.findByTenantId(tenantId).orElse(null);
      if(cfg!=null && cfg.json!=null && !cfg.json.isBlank()){
        var node = om.readTree(cfg.json);
        var xsd = node.get("xsd");
        if(xsd!=null){
          String policy = xsd.has("policy")? xsd.get("policy").asText("latest") : "latest";
          if("pinned".equalsIgnoreCase(policy)){
            var pinned = xsd.get("pinned");
            if(pinned!=null && pinned.has(tipo)){
              String v = pinned.get(tipo).asText();
              String p = registry.pathFor(tipo, v);
              if(p!=null) return p;
            }
          }
        }
      }
    }catch(Exception ignore){}
    // Default: latest for type
    return registry.latestPath(tipo);
  }
}
