
package com.fhalcom.facturacion.webhooks;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WebhookScheduler {
  private final WebhookService svc;
  public WebhookScheduler(WebhookService svc){ this.svc = svc; }

  @Scheduled(fixedDelay = 30000)
  public void retry(){ svc.retryPending(); }
}
