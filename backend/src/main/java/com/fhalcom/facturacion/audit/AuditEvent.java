package com.fhalcom.facturacion.audit;
import jakarta.persistence.*; import java.time.OffsetDateTime; import java.util.UUID;
@Entity @Table(name="audit_events")
public class AuditEvent {
  @Id public UUID id = java.util.UUID.randomUUID();
  public String tenantId; public String actor; public String action; public String resource; @Lob public String details;
  public String prevHash; public String hash; public OffsetDateTime at = OffsetDateTime.now();
}
