
package com.fhalcom.facturacion.company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface CompanyRepo extends JpaRepository<Company, UUID> {
  Optional<Company> findFirstByTenant_id(UUID tenantId);
  Optional<Company> findByRuc(String ruc);
}
