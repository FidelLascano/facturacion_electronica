
package com.fhalcom.facturacion.config;

import com.fhalcom.facturacion.repo.UserRepository;
import com.fhalcom.facturacion.repo.RoleRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {

  @Bean
  public SecurityFilterChain methodChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http,
                                         @Value("${app.jwtSecret}") String secret,
                                         com.facturacion.repo.UserRepository users) throws Exception {
    http.addFilterBefore(new org.springframework.web.filter.OncePerRequestFilter(){
      @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String h = req.getHeader("Authorization");
        if(h!=null && h.startsWith("Bearer ")){
          try{
            Jws<Claims> parsed = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secret.getBytes())).build().parseClaimsJws(h.substring(7));
            String email = parsed.getBody().getSubject();
            var u = users.findByEmail(email).orElse(null);
            if(u!=null){
              var auths = u.getRoles().stream().map(r-> new SimpleGrantedAuthority("ROLE_"+r.getName())).collect(Collectors.toList());
              var auth = new UsernamePasswordAuthenticationToken(email, null, auths);
              SecurityContextHolder.getContext().setAuthentication(auth);
            }
          }catch(Exception ignored){}
        }
        chain.doFilter(req,res);
      }
    }, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
