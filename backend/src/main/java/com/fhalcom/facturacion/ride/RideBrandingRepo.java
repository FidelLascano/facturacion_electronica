package com.fhalcom.facturacion.ride;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; import java.util.UUID;
public interface RideBrandingRepo extends JpaRepository<RideBranding, UUID>{
  Optional<RideBranding> findByTenantId(String tenantId);
}
