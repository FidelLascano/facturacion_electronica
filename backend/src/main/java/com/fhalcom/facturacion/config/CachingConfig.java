
package com.fhalcom.facturacion.config;
import com.github.benmanes.caffeine.cache.Caffeine; import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager; import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration;
@Configuration @EnableCaching
public class CachingConfig {
  @Bean public Caffeine<Object,Object> caffeine(){ return Caffeine.newBuilder().maximumSize(2000).expireAfterWrite(java.time.Duration.ofMinutes(10)); }
  @Bean public CacheManager cacheManager(Caffeine<Object,Object> cfg){ CaffeineCacheManager m = new CaffeineCacheManager("xsdIndex","jrxmlByTenantDoc"); m.setCaffeine(cfg); return m; }
}
