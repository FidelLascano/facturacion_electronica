package com.fhalcom.facturacion.ride;
import jakarta.persistence.*;
import java.util.UUID; import java.time.OffsetDateTime;
@Entity @Table(name="ride_branding", uniqueConstraints=@UniqueConstraint(columnNames={"tenant_id"}))
public class RideBranding {
  @Id public UUID id = java.util.UUID.randomUUID();
  @Column(name="tenant_id", unique=true) public String tenantId;
  @Lob public byte[] logo;
  public String contentType;
  public OffsetDateTime updatedAt = OffsetDateTime.now();
}
