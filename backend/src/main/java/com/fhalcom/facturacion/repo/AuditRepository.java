
package com.fhalcom.facturacion.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fhalcom.facturacion.domain.AuditEvent;
public interface AuditRepository extends JpaRepository<AuditEvent, Long> {}
