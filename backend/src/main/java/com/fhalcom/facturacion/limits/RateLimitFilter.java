
package com.fhalcom.facturacion.limits;
import com.bucket4j.*;
import com.fhalcom.facturacion.tenancy.TenantContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {
  private final boolean enabled;
  private final long perMin;
  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  public RateLimitFilter(@Value("${app.ratelimit.enabled:true}") boolean enabled,
                         @Value("${app.ratelimit.perMin:120}") long perMin){
    this.enabled = enabled; this.perMin = perMin;
  }

  private Bucket newBucket(){
    Refill refill = Refill.greedy(perMin, Duration.ofMinutes(1));
    Bandwidth limit = Bandwidth.classic(perMin, refill);
    return Bucket.builder().addLimit(limit).build();
  }

  @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    if(!enabled){ chain.doFilter(req,res); return; }
    String tenant = TenantContext.getTenant();
    Bucket b = buckets.computeIfAbsent(tenant==null?"default":tenant, t -> newBucket());
    if(!b.tryConsume(1)){
      ((HttpServletResponse)res).setStatus(429);
      res.getWriter().write("Rate limit exceeded");
      return;
    }
    chain.doFilter(req, res);
  }
}
