
package com.fhalcom.facturacion.me;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class MeController {
  @GetMapping("/me/permissions")
  public Map<String,Object> perms(){
    return Map.of("permissions", new String[]{"EINVOICE.WRITE"});
  }
}
