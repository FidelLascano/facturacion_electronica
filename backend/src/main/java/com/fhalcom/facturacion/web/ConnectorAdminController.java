package com.fhalcom.facturacion.web;
import org.springframework.web.bind.annotation.*;
import com.fhalcom.facturacion.connectors.*;
import java.util.*;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController @RequestMapping("/connectors")
public class ConnectorAdminController {
  private final ExternalConnectorRepo repo; private final ConnectorDeadLetterRepo dead;
  public ConnectorAdminController(ExternalConnectorRepo r, ConnectorDeadLetterRepo d){ this.repo=r; this.dead=d; }

  @GetMapping("/health")
  public List<Map<String,Object>> health(){
    List<Map<String,Object>> out = new ArrayList<>();
    for(ExternalConnector c : repo.findByEnabledTrue()){
      long dl = dead.countByConnectorId(c.id);
      out.add(Map.of("id", c.id, "name", c.name, "type", c.type, "dbType", c.dbType, "url", c.url, "enabled", c.enabled, "deadLetters", dl));
    }
    return out;
  }

  @GetMapping("/{id}/deadletters")
  public List<Map<String,Object>> deadletters(@PathVariable UUID id){
    List<Map<String,Object>> out = new ArrayList<>();
    for(ConnectorDeadLetter dl : dead.findTop100ByConnectorIdOrderByAtDesc(id)){
      out.add(Map.of("id", dl.id, "reason", dl.reason, "payload", dl.payload, "at", dl.at.toString()));
    }
    return out;
  }

  @DeleteMapping("/deadletters/{dlId}")
  public Map<String,Object> delete(@PathVariable UUID dlId){
    dead.deleteById(dlId);
    return Map.of("ok", true);
  }

  @PostMapping("/{id}/test")
  public Map<String,Object> test(@PathVariable UUID id){
    ExternalConnector c = repo.findById(id).orElseThrow();
    try{
      HikariConfig cfg = new HikariConfig();
      cfg.setJdbcUrl(c.url); cfg.setUsername(c.username); cfg.setPassword(c.passwordEnc);
      cfg.setMaximumPoolSize(1);
      try(HikariDataSource ds = new HikariDataSource(cfg)){
        JdbcTemplate jt = new JdbcTemplate(ds);
        List<Map<String,Object>> rows = jt.queryForList(c.pollingQuery + " LIMIT 1");
        Map<String,Object> row = rows.isEmpty()? Map.of() : rows.get(0);
        String xml = rows.isEmpty()? "" : InboundMapper.render(c.mappingJson, row);
        return Map.of("ok", true, "row", row, "xml", xml[:1000]);
      }
    }catch(Exception e){
      return Map.of("ok", false, "error", e.getMessage());
    }
  }
}
