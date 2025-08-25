
package com.fhalcom.facturacion.audit;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.stereotype.Component;
import com.fhalcom.facturacion.repo.AuditRepository;
import com.fhalcom.facturacion.domain.AuditEvent;

@Aspect @Component
public class AuditAspect {
  private final AuditRepository repo;
  public AuditAspect(AuditRepository r){ this.repo=r; }

  @AfterReturning("execution(* com.facturacion.web.EDocController.create(..))")
  public void onCreate(){ save("CREATE","EDOC"); }

  @AfterReturning("execution(* com.facturacion.web.EDocFlowController.send(..))")
  public void onSend(){ save("SEND","EDOC"); }

  @AfterReturning("execution(* com.facturacion.web.EDocFlowController.authorize(..))")
  public void onAuth(){ save("AUTHORIZE","EDOC"); }

  private void save(String action, String ent){ AuditEvent e = new AuditEvent(); e.action=action; e.entity=ent; e.entityId="-"; repo.save(e); }
}
