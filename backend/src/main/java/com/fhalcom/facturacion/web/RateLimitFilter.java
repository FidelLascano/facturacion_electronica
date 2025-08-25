
package com.fhalcom.facturacion.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {
  private static class Counter{ long windowStart; int count; }
  private final Map<String, Counter> map = new ConcurrentHashMap<>();
  private final int limit = 120; // per minute

  @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    String tenant = ((jakarta.servlet.http.HttpServletRequest)req).getHeader("X-Tenant-ID");
    if(tenant==null) tenant="default";
    Counter c = map.computeIfAbsent(tenant, t-> new Counter());
    long now = Instant.now().getEpochSecond();
    if(c.windowStart==0 || now - c.windowStart >= 60){ c.windowStart = now; c.count = 0; }
    c.count++;
    if(c.count > limit){
      ((HttpServletResponse)res).setStatus(429);
      res.getWriter().write("Rate limit exceeded");
      return;
    }
    chain.doFilter(req, res);
  }
}
