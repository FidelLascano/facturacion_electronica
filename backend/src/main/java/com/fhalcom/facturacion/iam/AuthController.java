
package com.fhalcom.facturacion.iam;
import com.fhalcom.facturacion.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/auth")
public class AuthController {
  private final UserRepo users; private final PasswordEncoder enc; private final JwtService jwt;
  public AuthController(UserRepo users, PasswordEncoder enc, JwtService jwt){ this.users=users; this.enc=enc; this.jwt=jwt; }

  public record LoginReq(String email, String password){}
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginReq req){
    var user = users.findByEmail(req.email()).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    if(!Boolean.TRUE.equals(user.active) || !enc.matches(req.password(), user.password_hash)){
      throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    String token = jwt.issue(user.email, Map.of("uid", user.id.toString()));
    return ResponseEntity.ok(Map.of("token", token));
  }
}
