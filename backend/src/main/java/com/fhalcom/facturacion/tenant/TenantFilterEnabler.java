
package com.fhalcom.facturacion.tenant;
import jakarta.servlet.*; import jakarta.servlet.http.*; import java.io.IOException;
import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter;
import org.hibernate.Session; import jakarta.persistence.EntityManager; import org.springframework.beans.factory.annotation.Autowired;
@Component
public class TenantFilterEnabler extends OncePerRequestFilter {
  @Autowired private EntityManager em;
  @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
    String tenant = TenantContext.getCurrentTenant();
    try{
      if(tenant!=null){
        try{ em.unwrap(Session.class).enableFilter("tenantFilter").setParameter("tenantId", tenant); }catch(Exception ignored){}
      }
      chain.doFilter(req,res);
    } finally { try{ em.unwrap(Session.class).disableFilter("tenantFilter"); }catch(Exception ignored){} }
  }
}
