
package com.fhalcom.facturacion.web;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.fhalcom.facturacion.repo.WebhookSubRepo;
import com.fhalcom.facturacion.domain.WebhookSubscription;

@RestController @RequestMapping("/webhooks")
public class WebhookController {
  private final WebhookSubRepo subs;
  public WebhookController(WebhookSubRepo s){ this.subs=s; }

  @PostMapping("/subscriptions") public WebhookSubscription add(@RequestBody Map<String,String> b){
    WebhookSubscription w = new WebhookSubscription(); w.url=b.get("url"); w.secret=b.getOrDefault("secret","secret"); return subs.save(w);
  }
  @GetMapping("/subscriptions") public List<WebhookSubscription> list(){ return subs.findAll(); }
}
