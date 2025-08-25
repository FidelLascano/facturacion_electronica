
package com.fhalcom.facturacion.admin;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/admin/cache")
public class CacheController {
  @PostMapping("/invalidate") public Map<String,Object> invalidate(){ return Map.of("status","ok"); }
}
