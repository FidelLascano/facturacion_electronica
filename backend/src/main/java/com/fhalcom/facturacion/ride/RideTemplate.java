package com.fhalcom.facturacion.ride;
import jakarta.persistence.*;
import java.util.UUID;
import java.time.OffsetDateTime;
@Entity @Table(name="ride_template", uniqueConstraints=@UniqueConstraint(columnNames={"tenant_id","doc_type"}))
public class RideTemplate {
  @Id public UUID id = java.util.UUID.randomUUID();
  @Column(name="tenant_id") public String tenantId;
  @Column(name="doc_type") public String docType;
  @Lob public String jrxml;
  public OffsetDateTime updatedAt = OffsetDateTime.now();
}
