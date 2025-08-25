
package com.fhalcom.facturacion.tenancy;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class TenantFilter implements Filter {
  @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    String t = req.getHeader("X-Tenant-ID");
    if(t==null || t.isBlank()) t = "demo";
    try { TenantContext.setTenant(t); chain.doFilter(request,response); }
    finally { TenantContext.clear(); }
  }
}
