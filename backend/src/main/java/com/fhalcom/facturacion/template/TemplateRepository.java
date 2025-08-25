
package com.fhalcom.facturacion.template;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface TemplateRepository extends JpaRepository<Template, String> {
  Optional<Template> findByTenantIdAndDocType(String tenantId, String docType);
}
