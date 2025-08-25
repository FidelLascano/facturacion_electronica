
package com.fhalcom.facturacion.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fhalcom.facturacion.domain.EDoc;
public interface EDocRepository extends JpaRepository<EDoc, Long> {}
