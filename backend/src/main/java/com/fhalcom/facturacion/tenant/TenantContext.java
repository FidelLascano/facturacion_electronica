
package com.fhalcom.facturacion.tenant;
public class TenantContext {
  private static final ThreadLocal<String> CURR = new ThreadLocal<>();
  public static void setCurrentTenant(String t){ CURR.set(t); }
  public static String getCurrentTenant(){ return CURR.get(); }
  public static void clear(){ CURR.remove(); }
}
