
package com.fhalcom.facturacion.audit;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@org.springframework.security.access.prepost.PreAuthorize("@perm.hasPermission(authentication, null, 'ADMIN.MANAGE')") @RequestMapping("/audit")
public class AuditController {
  private final JdbcTemplate jdbc;
  public AuditController(JdbcTemplate jdbc){ this.jdbc = jdbc; }

  @GetMapping
  public List<Map<String,Object>> list(){
    return jdbc.queryForList("select id, action, entity, entity_id, at from audit_event order by at desc limit 200");
  }
}
