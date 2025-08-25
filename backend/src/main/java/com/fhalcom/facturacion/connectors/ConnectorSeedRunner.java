package com.fhalcom.facturacion.connectors;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.nio.file.*; import java.util.*; import java.util.stream.Collectors;

@Component
public class ConnectorSeedRunner implements CommandLineRunner {
  private final ExternalConnectorRepo repo;
  public ConnectorSeedRunner(ExternalConnectorRepo r){ this.repo=r; }
  @Override public void run(String... args) throws Exception {
    if(!"1".equals(System.getenv().getOrDefault("CONNECTOR_SEED","0"))) return;
    String tenant = System.getenv().getOrDefault("TENANT_ID","default");
    String[] docTypes = new String[]{"factura","notaCredito","notaDebito","guiaRemision","comprobanteRetencion","liquidacionCompra"};
    for(String driver: new String[]{"PG","MYSQL","ORA"}){
      String url = System.getenv(driver+"_URL");
      if(url==null||url.isBlank()) continue;
      ExternalConnector c = new ExternalConnector();
      c.tenantId = tenant; c.name = driver+"_seed"; c.type="JDBC"; c.dbType = driver.toLowerCase();
      c.url = url; c.username = System.getenv(driver+"_USER"); c.passwordEnc = System.getenv(driver+"_PASSWORD");
      c.pollingQuery = System.getenv().getOrDefault(driver+"_POLLING_QUERY","SELECT * FROM integration_invoices WHERE status='READY'");
      c.intervalSec = 60;
      // pick mapping for factura by default
      try{
        String mapping = new String(ConnectorSeedRunner.class.getResourceAsStream("/connectors/mapping/factura.json").readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        c.mappingJson = mapping;
      }catch(Exception ignore){}
      repo.save(c);
    }
  }
}
