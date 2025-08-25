package com.fhalcom.facturacion.audit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*; import java.util.UUID;
public interface AuditRepo extends JpaRepository<AuditEvent, UUID>{
  java.util.List<AuditEvent> findByTenantIdAndAtBetweenOrderByAtAsc(String tenantId, java.time.OffsetDateTime from, java.time.OffsetDateTime to);
  AuditEvent findTop1ByTenantIdOrderByAtDesc(String tenantId);
}
