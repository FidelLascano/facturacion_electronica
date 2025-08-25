package com.fhalcom.facturacion.connectors;
import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="external_connectors")
public class ExternalConnector {
  @Id public UUID id = java.util.UUID.randomUUID();
  public String tenantId;
  public String name;
  public String type; // JDBC
  public String dbType; // postgres|mysql|oracle
  public String url;
  public String username;
  public String passwordEnc; // AES-GCM expected
  public String pollingQuery;
  public Integer intervalSec;
  @Lob public String mappingJson;
  public boolean enabled = true;
}
