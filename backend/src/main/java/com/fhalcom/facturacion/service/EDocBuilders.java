
package com.fhalcom.facturacion.service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;

public class EDocBuilders {
  public static Document factura(String version, String clave, String ruc) {
    return build("factura", version, clave, ruc);
  }
  public static Document notaCredito(String version, String clave, String ruc){
    return build("notaCredito", version, clave, ruc);
  }
  public static Document notaDebito(String version, String clave, String ruc){
    return build("notaDebito", version, clave, ruc);
  }
  public static Document guia(String version, String clave, String ruc){
    return build("guiaRemision", version, clave, ruc);
  }
  public static Document retencion(String version, String clave, String ruc){
    return build("retencion", version, clave, ruc);
  }
  public static Document liquidacion(String version, String clave, String ruc){
    return build("liquidacion", version, clave, ruc);
  }

  private static Document build(String root, String version, String clave, String ruc){
    try{
      var dbf = DocumentBuilderFactory.newInstance(); dbf.setNamespaceAware(true);
      Document doc = dbf.newDocumentBuilder().newDocument();
      Element r = doc.createElement(root); r.setAttribute("id","comprobante"); r.setAttribute("version", version); doc.appendChild(r);
      Element it = doc.createElement("infoTributaria"); r.appendChild(it);
      Element ca = doc.createElement("claveAcceso"); ca.setTextContent(clave); it.appendChild(ca);
      if(ruc!=null){ Element rr = doc.createElement("ruc"); rr.setTextContent(ruc); it.appendChild(rr); }
      Element inf = doc.createElement("info"); r.appendChild(inf);
      Element sec = doc.createElement("secuencial"); sec.setTextContent("000000001"); inf.appendChild(sec);
      Element tot = doc.createElement("importeTotal"); tot.setTextContent("0.00"); inf.appendChild(tot);
      return doc;
    }catch(Exception e){ throw new RuntimeException(e); }
  }
}
