package com.fhalcom.facturacion.audit;
import org.springframework.stereotype.Service;
@Service
public class AuditService {
  private final AuditRepo repo;
  public AuditService(AuditRepo r){ this.repo=r; }
  public void record(String tenant, String actor, String action, String resource, String details){
    try{
      var e = new AuditEvent(); e.tenantId=tenant; e.actor=actor; e.action=action; e.resource=resource; e.details=details;
      var last = repo.findTop1ByTenantIdOrderByAtDesc(tenant);
      e.prevHash = last!=null? last.hash : null;
      e.hash = sha256((e.prevHash==null?"":e.prevHash)+tenant+actor+action+resource+details+e.at.toString());
      repo.save(e);
    }catch(Exception ex){ throw new RuntimeException(ex); }
  }
  public boolean verify(String tenant){
    var list = repo.findByTenantIdAndAtBetweenOrderByAtAsc(tenant, java.time.OffsetDateTime.MIN, java.time.OffsetDateTime.MAX);
    String prev=null;
    for(var e : list){
      String h = sha256((prev==null?"":prev)+e.tenantId+e.actor+e.action+e.resource+e.details+e.at.toString());
      if(!h.equals(e.hash)) return false; prev=e.hash;
    }
    return true;
  }
  static String sha256(String s) throws Exception {
    var md = java.security.MessageDigest.getInstance("SHA-256");
    byte[] b = md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    StringBuilder sb = new StringBuilder(); for(byte x: b) sb.append(String.format("%02x", x)); return sb.toString();
  }
}
