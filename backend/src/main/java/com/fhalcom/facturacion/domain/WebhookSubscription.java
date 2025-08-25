
package com.fhalcom.facturacion.domain;
import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="webhook_subscription")
public class WebhookSubscription {
  @Id @GeneratedValue(strategy=GenerationType.AUTO) public UUID id;
  @Column(nullable=false) public String url;
  @Column(nullable=false) public String secret;
  public boolean active = true;
}
