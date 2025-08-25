package com.fhalcom.facturacion.connectors;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
@Entity @Table(name="connector_dead_letter")
public class ConnectorDeadLetter {
  @Id public UUID id = java.util.UUID.randomUUID();
  public UUID connectorId;
  public String reason;
  @Lob public String payload;
  public OffsetDateTime at = OffsetDateTime.now();
}
