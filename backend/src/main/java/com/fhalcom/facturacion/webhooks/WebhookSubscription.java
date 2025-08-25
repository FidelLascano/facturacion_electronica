
package com.fhalcom.facturacion.webhooks;
import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="webhook_subscription")
public class WebhookSubscription {
  @Id public UUID id;
  public UUID tenant_id;
  public String url;
  public String secret;
  public Boolean active = true;
}
