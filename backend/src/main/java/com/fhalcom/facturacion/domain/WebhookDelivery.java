
package com.fhalcom.facturacion.domain;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name="webhook_delivery")
public class WebhookDelivery {
  @Id @GeneratedValue(strategy=GenerationType.AUTO) public UUID id;
  @Column(nullable=false) public UUID subscriptionId;
  @Column(nullable=false) public String event;
  @Column(columnDefinition="text") public String payload;
  public String status = "PENDING"; public Integer attempts = 0;
  public OffsetDateTime createdAt = OffsetDateTime.now();
}
