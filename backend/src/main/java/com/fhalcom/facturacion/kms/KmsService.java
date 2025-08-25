
package com.fhalcom.facturacion.kms;
import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KmsService {
  private final String mode, addr, token;
  private final OkHttpClient http = new OkHttpClient();
  private final ObjectMapper om = new ObjectMapper();
  public KmsService(@Value("${app.kmsMode:dev}") String mode,
                    @Value("${VAULT_ADDR:http://localhost:8200}") String addr,
                    @Value("${VAULT_TOKEN:root}") String token){
    this.mode=mode; this.addr=addr; this.token=token;
  }
  public String pinForCompany(String companyId){
    if(!"vault".equalsIgnoreCase(mode)) return "123456";
    try{
      String url = addr + "/v1/secret/data/certs/"+companyId;
      Request req = new Request.Builder().url(url).get().addHeader("X-Vault-Token", token).build();
      try(Response res = http.newCall(req).execute()){
        if(!res.isSuccessful()) throw new RuntimeException("Vault: "+res.code());
        JsonNode n = om.readTree(res.body().string());
        return n.path("data").path("data").path("pin").asText();
      }
    }catch(Exception e){ throw new RuntimeException(e); }
  }
}
