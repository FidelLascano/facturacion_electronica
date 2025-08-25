
package com.fhalcom.facturacion.limits;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class QuotaService {
  private final StringRedisTemplate redis;
  private final int dailyLimit;
  public QuotaService(StringRedisTemplate t){
    this.redis=t; this.dailyLimit = Integer.parseInt(System.getenv().getOrDefault("APP_DAILY_QUOTA","1000"));
  }
  public boolean allow(String tenant){
    String key = "quota:"+tenant+":"+LocalDate.now();
    Long val = redis.opsForValue().increment(key);
    return val!=null && val <= dailyLimit;
  }
}
