
package com.fhalcom.facturacion.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import com.fhalcom.facturacion.domain.Company;
public interface CompanyRepository extends JpaRepository<Company, UUID> {
  Optional<Company> findByRuc(String ruc);
}
