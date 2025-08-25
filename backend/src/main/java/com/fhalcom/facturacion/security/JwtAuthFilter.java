
package com.fhalcom.facturacion.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwt;
  public JwtAuthFilter(JwtService jwt){ this.jwt = jwt; }

  @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String auth = request.getHeader("Authorization");
    if(auth!=null && auth.startsWith("Bearer ")){
      String token = auth.substring(7);
      try{
        Jws<Claims> parsed = jwt.parse(token);
        String sub = parsed.getBody().getSubject();
        String uid = (String) parsed.getBody().get("uid");
        var authn = (Authentication) new UsernamePasswordAuthenticationToken(sub, null, List.of());
        ((UsernamePasswordAuthenticationToken)authn).setDetails(Map.of("uid", uid));
        SecurityContextHolder.getContext().setAuthentication(authn);
      }catch(Exception ignored){}
    }
    filterChain.doFilter(request, response);
  }
}
