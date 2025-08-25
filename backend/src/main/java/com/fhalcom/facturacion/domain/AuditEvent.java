
package com.fhalcom.facturacion.domain;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity @Table(name="audit_event")
public class AuditEvent {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
  public String action; public String entity; public String entityId;
  public OffsetDateTime at = OffsetDateTime.now();
}
