
package com.fhalcom.facturacion.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.Duration;
import java.util.Base64;

public class Totp {
  public static String generateSecret() {
    byte[] b = new byte[20];
    new java.security.SecureRandom().nextBytes(b);
    return Base64.getEncoder().encodeToString(b);
  }
  public static String code(String base64Secret, long timeSeconds){
    byte[] key = Base64.getDecoder().decode(base64Secret);
    long counter = timeSeconds / 30;
    byte[] msg = ByteBuffer.allocate(8).putLong(counter).array();
    try{
      Mac mac = Mac.getInstance("HmacSHA1");
      mac.init(new SecretKeySpec(key, "HmacSHA1"));
      byte[] h = mac.doFinal(msg);
      int offset = h[h.length-1] & 0xF;
      int bin = ((h[offset] & 0x7f) << 24) | ((h[offset+1] & 0xff) << 16) | ((h[offset+2] & 0xff) << 8) | (h[offset+3] & 0xff);
      int otp = bin % 1000000;
      return String.format("%06d", otp);
    }catch(Exception e){ throw new RuntimeException(e); }
  }
  public static boolean verify(String base64Secret, String code){
    long now = Instant.now().getEpochSecond();
    for(long t = now - 60; t <= now + 60; t+=30){
      if(code.equals(code(base64Secret, t))) return true;
    }
    return false;
  }
}
