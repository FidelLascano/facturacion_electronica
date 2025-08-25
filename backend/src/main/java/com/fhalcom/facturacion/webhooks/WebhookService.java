
package com.fhalcom.facturacion.webhooks;
import com.fhalcom.facturacion.repo.WebhookSubRepo;
import com.fhalcom.facturacion.repo.WebhookDelivRepo;
import com.fhalcom.facturacion.domain.WebhookSubscription;
import com.fhalcom.facturacion.domain.WebhookDelivery;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import okhttp3.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class WebhookService {
  private final WebhookSubRepo subs; private final WebhookDelivRepo dels; private final OkHttpClient http = new OkHttpClient();
  public WebhookService(WebhookSubRepo s, WebhookDelivRepo d){ this.subs=s; this.dels=d; }

  public void publish(String event, Map<String,Object> payload){
    List<WebhookSubscription> S = subs.findAll();
    S.forEach(s -> enqueue(s, event, payload));
  }

  private void enqueue(WebhookSubscription s, String event, Map<String,Object> payload){
    try{
      WebhookDelivery d = new WebhookDelivery();
      d.subscriptionId = s.id; d.event = event;
      d.payload = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload);
      dels.save(d); deliver(s, d);
    }catch(Exception ignored){}
  }

  public void retry(){ dels.findByStatus("PENDING").forEach(d -> subs.findById(d.subscriptionId).ifPresent(s -> deliver(s,d))); }

  private void deliver(WebhookSubscription s, WebhookDelivery d){
    try{
      RequestBody body = RequestBody.create(d.payload.getBytes(StandardCharsets.UTF_8), MediaType.parse("application/json"));
      String sig = hmac(d.payload, s.secret);
      Request req = new Request.Builder().url(s.url).post(body).addHeader("X-Signature", sig).build();
      try(Response res = http.newCall(req).execute()){
        d.status = res.isSuccessful() ? "DELIVERED":"PENDING";
        d.attempts = d.attempts + 1; dels.save(d);
      }
    }catch(Exception e){ d.status="PENDING"; d.attempts = d.attempts + 1; dels.save(d); }
  }
  private static String hmac(String data, String secret){
    try{
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      byte[] out = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder(); for(byte b: out) sb.append(String.format("%02x", b)); return sb.toString();
    }catch(Exception e){ throw new RuntimeException(e); }
  }
}
