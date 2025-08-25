package com.fhalcom.facturacion.tenant;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name="tenant_config")
public class TenantConfig {
  @Id public UUID id = java.util.UUID.randomUUID();
  @Column(unique=true) public String tenantId;
  @Lob public String json;
}
