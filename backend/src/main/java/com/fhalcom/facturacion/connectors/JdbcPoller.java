package com.fhalcom.facturacion.connectors;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.*;
import io.micrometer.core.instrument.MeterRegistry;
import com.fhalcom.facturacion.connectors.InboundMapper;
import com.fhalcom.facturacion.inbound.InboundService;

@Component
public class JdbcPoller {
  private final ExternalConnectorRepo repo; private final ConnectorDeadLetterRepo dead; private final InboundService inbound; private final MeterRegistry metrics;
  public JdbcPoller(ExternalConnectorRepo r, ConnectorDeadLetterRepo d, InboundService i, MeterRegistry m){ this.repo=r; this.dead=d; this.inbound=i; this.metrics=m; }

  @Scheduled(fixedDelay=60000)
  public void poll(){
    for(ExternalConnector c : repo.findByEnabledTrue()){
      long start = System.currentTimeMillis();
      int rows = 0; int errors = 0;
      try{
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(c.url); cfg.setUsername(c.username); cfg.setPassword(c.passwordEnc);
        cfg.setMaximumPoolSize(1);
        try(HikariDataSource ds = new HikariDataSource(cfg)){
          JdbcTemplate jt = new JdbcTemplate(ds);
          List<Map<String,Object>> list = jt.queryForList(c.pollingQuery);
          for(Map<String,Object> row : list){
            try{
              String xml = InboundMapper.render(c.mappingJson, row);
              String docType = com.fasterxml.jackson.databind.json.JsonMapper.builder().build().readTree(c.mappingJson).get("docType").asText();
              inbound.ingest(c.tenantId!=null?c.tenantId:"default", docType, xml);
              rows++;
            }catch(Exception ex){
              errors++;
              ConnectorDeadLetter dl = new ConnectorDeadLetter();
              dl.connectorId = c.id;
              dl.reason = ex.getMessage();
              dl.payload = row.toString();
              dead.save(dl);
            }
          }
        }
      }catch(Exception ex){
        errors++;
        ConnectorDeadLetter dl = new ConnectorDeadLetter(); dl.connectorId=c.id; dl.reason="CONNECTION:"+ex.getMessage(); dl.payload=""; dead.save(dl);
      }finally{
        long dur = System.currentTimeMillis()-start;
        metrics.timer("connectors_poll_duration", "name", c.name).record(dur, java.util.concurrent.TimeUnit.MILLISECONDS);
        metrics.counter("connectors_rows_total", "name", c.name).increment(rows);
        metrics.counter("connectors_errors_total", "name", c.name).increment(errors);
      }
    }
  }
}
