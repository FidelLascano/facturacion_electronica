package com.fhalcom.facturacion.connectors;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.util.UUID;
public interface ExternalConnectorRepo extends JpaRepository<ExternalConnector, UUID>{
  List<ExternalConnector> findByEnabledTrue();
}
