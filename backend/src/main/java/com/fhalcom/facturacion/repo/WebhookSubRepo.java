
package com.fhalcom.facturacion.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import com.fhalcom.facturacion.domain.WebhookSubscription;
public interface WebhookSubRepo extends JpaRepository<WebhookSubscription, UUID> {}
