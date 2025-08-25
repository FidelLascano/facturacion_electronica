
package com.fhalcom.facturacion.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.*;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final SecretKey key;
  public JwtService(@org.springframework.beans.factory.annotation.Value("${app.jwtSecret}") String secret){
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
  }
  public String issue(String subject, Map<String,Object> claims){
    return Jwts.builder()
      .setSubject(subject)
      .addClaims(claims)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis()+ 12*60*60*1000))
      .signWith(key, SignatureAlgorithm.HS256).compact();
  }
  public Jws<Claims> parse(String token){ return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); }
}
