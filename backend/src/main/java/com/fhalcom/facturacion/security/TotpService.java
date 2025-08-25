
package com.fhalcom.facturacion.security;
import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class TotpService {
  private final TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(30), 6);

  public SecretKey newSecret() {
    try { KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA1"); return keyGenerator.generateKey(); }
    catch(Exception e){ throw new RuntimeException(e); }
  }
  public String uri(String label, SecretKey secret){
    String secretB32 = Base64.getEncoder().encodeToString(secret.getEncoded());
    return "otpauth://totp/" + URLEncoder.encode(label, StandardCharsets.UTF_8) + "?secret=" + secretB32 + "&issuer=FacturacionEC";
  }
}
