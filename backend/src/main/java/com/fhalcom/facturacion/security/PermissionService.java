
package com.fhalcom.facturacion.security;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class PermissionService {
  private final JdbcTemplate jdbc;
  public PermissionService(JdbcTemplate jdbc){ this.jdbc = jdbc; }

  @Transactional(readOnly = true)
  public boolean hasPermission(UUID userId, String code){
    String sql = """
      select 1
      from user_group_role ugr
      join role_permission rp on rp.role_id = ugr.role_id
      join permission p on p.id = rp.permission_id
      where ugr.user_id = ? and p.code = ?
      limit 1
    """;
    List<Integer> r = jdbc.query(sql, ps -> { ps.setObject(1, userId); ps.setString(2, code); }, (rs, i) -> 1);
    return !r.isEmpty();
  }
}
