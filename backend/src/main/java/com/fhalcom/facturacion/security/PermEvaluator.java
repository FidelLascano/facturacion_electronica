
package com.fhalcom.facturacion.security;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@Component("perm")
public class PermEvaluator implements PermissionEvaluator {
  private final PermissionService perms;
  public PermEvaluator(PermissionService perms){ this.perms = perms; }
  @Override public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission){
    try{
      Map details = (Map) auth.getDetails();
      String uid = (String) details.get("uid");
      return perms.hasPermission(UUID.fromString(uid), permission.toString());
    }catch(Exception e){ return false; }
  }
  @Override public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission){
    return hasPermission(authentication, null, permission);
  }
}
