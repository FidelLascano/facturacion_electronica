
package com.fhalcom.facturacion.xsd;
public class XsdResolver {
  public static String pathFor(String tipo, String version){
    String root = tipo.equals("FACTURA")?"factura":
                  tipo.equals("NOTA_CREDITO")?"notaCredito":
                  tipo.equals("NOTA_DEBITO")?"notaDebito":
                  tipo.equals("GUIA_REMISION")?"guiaRemision":
                  tipo.equals("RETENCION")?"retencion":
                  tipo.equals("LIQUIDACION")?"liquidacion":tipo.toLowerCase();
    return "/xsd/vendor/"+root+"_"+version+".xsd";
  }
}
