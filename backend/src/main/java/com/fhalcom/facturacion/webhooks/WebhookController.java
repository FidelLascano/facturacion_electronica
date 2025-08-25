
package com.fhalcom.facturacion.webhooks;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@org.springframework.security.access.prepost.PreAuthorize("@perm.hasPermission(authentication, null, 'ADMIN.MANAGE')") @RequestMapping("/webhooks")
public class WebhookController {
  private final WebhookSubRepo subs;
  private final WebhookDelivRepo dels;
  public WebhookController(WebhookSubRepo subs, WebhookDelivRepo dels){ this.subs=subs; this.dels=dels; }

  @PostMapping("/subscriptions")
  public WebhookSubscription create(@RequestBody Map<String,String> body){
    WebhookSubscription s = new WebhookSubscription();
    s.id = java.util.UUID.randomUUID();
    s.tenant_id = java.util.UUID.fromString("00000000-0000-0000-0000-000000000001");
    s.url = body.get("url");
    s.secret = body.getOrDefault("secret","secret");
    s.active = true;
    return subs.save(s);
  }
  @GetMapping("/subscriptions")
  public List<WebhookSubscription> list(){ return subs.findAll(); }

  @GetMapping("/deliveries")
  public List<WebhookDelivery> deliveries(){ return dels.findAll(); }
}
