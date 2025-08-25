
package com.fhalcom.facturacion.web;

import org.springframework.web.bind.annotation.*;
import com.fhalcom.facturacion.repo.UserRepository;
import com.fhalcom.facturacion.security.Totp;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;

@RestController @RequestMapping("/mfa")
public class MfaController {
  private final UserRepository users;
  public MfaController(UserRepository u){ this.users=u; }

  @PostMapping("/setup")
  public ResponseEntity<?> setup(@AuthenticationPrincipal org.springframework.security.core.userdetails.User u){
    var ent = users.findByEmail(u.getUsername()).orElseThrow();
    String secret = Totp.generateSecret();
    // store or upsert
    try(var con = java.sql.DriverManager.getConnection(System.getenv("APP_DB_URL"), System.getenv("APP_DB_USER"), System.getenv("APP_DB_PASS"))){
      try(var ps = con.prepareStatement("insert into user_mfa(user_id, secret, enabled) values (?,?,false) on conflict (user_id) do update set secret=excluded.secret, enabled=false")){
        ps.setLong(1, ent.getId()); ps.setString(2, secret); ps.executeUpdate();
      }
    }catch(Exception e){ throw new RuntimeException(e); }
    String uri = "otpauth://totp/FacturacionEcuador:"+u.getUsername()+"?secret="+secret+"&issuer=FacturacionEcuador&digits=6&period=30";
    return ResponseEntity.ok(java.util.Map.of("secret", secret, "otpauth", uri));
  }

  @PostMapping("/verify")
  public ResponseEntity<?> verify(@AuthenticationPrincipal org.springframework.security.core.userdetails.User u, @RequestParam String code){
    var ent = users.findByEmail(u.getUsername()).orElseThrow();
    try(var con = java.sql.DriverManager.getConnection(System.getenv("APP_DB_URL"), System.getenv("APP_DB_USER"), System.getenv("APP_DB_PASS"))){
      String secret = null;
      try(var ps = con.prepareStatement("select secret from user_mfa where user_id=?")){
        ps.setLong(1, ent.getId());
        try(var rs = ps.executeQuery()){
          if(rs.next()) secret = rs.getString(1);
        }
      }
      if(secret==null) return ResponseEntity.badRequest().body(java.util.Map.of("error","MFA not initialized"));
      boolean ok = Totp.verify(secret, code);
      if(ok){
        try(var ps2 = con.prepareStatement("update user_mfa set enabled=true where user_id=?")){
          ps2.setLong(1, ent.getId()); ps2.executeUpdate();
        }
        return ResponseEntity.ok(java.util.Map.of("status","enabled"));
      } else {
        return ResponseEntity.status(401).body(java.util.Map.of("error","invalid code"));
      }
    }catch(Exception e){ throw new RuntimeException(e); }
  }
}
