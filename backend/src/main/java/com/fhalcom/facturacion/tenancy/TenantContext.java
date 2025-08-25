
package com.fhalcom.facturacion.tenancy;
public class TenantContext {
  private static final ThreadLocal<String> tenant = new ThreadLocal<>();
  public static void setTenant(String t){ tenant.set(t); }
  public static String getTenant(){ return tenant.get(); }
  public static void clear(){ tenant.remove(); }
}
