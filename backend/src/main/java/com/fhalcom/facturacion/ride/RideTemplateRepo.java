package com.fhalcom.facturacion.ride;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface RideTemplateRepo extends JpaRepository<RideTemplate, UUID>{
  Optional<RideTemplate> findByTenantIdAndDocType(String tenantId, String docType);
}
