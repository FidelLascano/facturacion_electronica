package com.fhalcom.facturacion.connectors;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.UUID;
public interface ConnectorDeadLetterRepo extends JpaRepository<ConnectorDeadLetter, UUID>{
  long countByConnectorId(UUID connectorId);
  java.util.List<ConnectorDeadLetter> findTop100ByConnectorIdOrderByAtDesc(UUID connectorId);
}
