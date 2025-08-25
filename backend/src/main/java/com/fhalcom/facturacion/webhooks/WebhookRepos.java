
package com.fhalcom.facturacion.webhooks;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface WebhookSubRepo extends JpaRepository<WebhookSubscription, UUID> {
  List<WebhookSubscription> findByTenant_idAndActive(UUID tenant, boolean active);
}
public interface WebhookDelivRepo extends JpaRepository<WebhookDelivery, UUID> {
  List<WebhookDelivery> findByStatus(String status);
}
