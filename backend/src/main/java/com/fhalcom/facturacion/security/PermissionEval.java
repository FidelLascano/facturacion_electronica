
package com.fhalcom.facturacion.security;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Set;

@Component("perm")
public class PermissionEval implements PermissionEvaluator {
  @Override public boolean hasPermission(Authentication auth, Object target, Object perm){
    if(auth==null) return false;
    var as = auth.getAuthorities();
    if(as.stream().anyMatch(a-> a.getAuthority().equals("ROLE_ADMIN"))) return true;
    // Simplified mapping: ROLE_USER has READ, not WRITE
    String p = String.valueOf(perm);
    if(p.equals("EINVOICE.READ")) return as.stream().anyMatch(a-> a.getAuthority().equals("ROLE_USER"));
    return false;
  }
  @Override public boolean hasPermission(Authentication a, Serializable id, String type, Object p){
    return hasPermission(a, null, p);
  }
}
