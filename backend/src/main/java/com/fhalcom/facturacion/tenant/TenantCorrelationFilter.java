
package com.fhalcom.facturacion.tenant;
import jakarta.servlet.*; import jakarta.servlet.http.*; import java.io.IOException;
import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter;
@Component
public class TenantCorrelationFilter extends OncePerRequestFilter {
  @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
    String tenant = req.getHeader("X-Tenant-Id");
    if(tenant==null || tenant.isBlank()){ res.setStatus(400); res.getWriter().write("Missing X-Tenant-Id"); return; }
    try{ TenantContext.setCurrentTenant(tenant); chain.doFilter(req,res); } finally { TenantContext.clear(); }
  }
}
