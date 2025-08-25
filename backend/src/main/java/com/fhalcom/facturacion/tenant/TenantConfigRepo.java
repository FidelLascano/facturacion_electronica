package com.fhalcom.facturacion.tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface TenantConfigRepo extends JpaRepository<TenantConfig, UUID>{
  Optional<TenantConfig> findByTenantId(String tenantId);
}
