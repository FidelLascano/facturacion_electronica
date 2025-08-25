
package com.fhalcom.facturacion.signature;
import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Component;
@Component
public class SignatureHotReloader {
  private volatile long last = 0L;
  @Scheduled(fixedDelay = 60000)
  public void checkAndReload(){ /* best-effort: check sources and refresh providers */ last = System.currentTimeMillis(); }
  public void checkAndReloadNow(){ checkAndReload(); }
}
