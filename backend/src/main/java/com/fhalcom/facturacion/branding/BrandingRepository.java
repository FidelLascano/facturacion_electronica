
package com.fhalcom.facturacion.branding;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface BrandingRepository extends JpaRepository<Branding, String> {
  Optional<Branding> findByTenantId(String tenantId);
}
