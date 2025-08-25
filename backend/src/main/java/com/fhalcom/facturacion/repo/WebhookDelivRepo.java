
package com.fhalcom.facturacion.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
import com.fhalcom.facturacion.domain.WebhookDelivery;
public interface WebhookDelivRepo extends JpaRepository<WebhookDelivery, UUID> {
  List<WebhookDelivery> findByStatus(String status);
}
