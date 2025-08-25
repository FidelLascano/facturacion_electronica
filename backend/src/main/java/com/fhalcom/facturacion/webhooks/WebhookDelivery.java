
package com.fhalcom.facturacion.webhooks;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name="webhook_delivery")
public class WebhookDelivery {
  @Id public UUID id;
  public UUID subscription_id;
  public String event_code;
  @Column(columnDefinition="jsonb")
  public String payload;
  public String status;
  public Integer attempts;
  public OffsetDateTime created_at;
}
