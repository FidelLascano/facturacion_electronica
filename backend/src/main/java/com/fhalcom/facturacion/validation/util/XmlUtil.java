package com.fhalcom.facturacion.validation.util;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.charset.StandardCharsets;

public final class XmlUtil {
  private XmlUtil(){}

  public static byte[] asBytes(Object o){
    if(o == null) return null;
    if(o instanceof byte[]) return (byte[])o;
    if(o instanceof String) return ((String)o).getBytes(StandardCharsets.UTF_8);
    if(o instanceof org.w3c.dom.Document){
      return toBytes((Document)o);
    }
    return null;
  }

  public static Document parse(byte[] xml){
    try{
      var dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      return dbf.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(xml));
    }catch(Exception e){ throw new RuntimeException(e); }
  }

  public static String peekRootElementName(byte[] xml){
    try{
      var d = parse(xml);
      return d.getDocumentElement()!=null? d.getDocumentElement().getNodeName() : null;
    }catch(Exception e){ return null; }
  }

  public static String mapRootToDocType(String root){
    if(root == null) return null;
    String r = root.toLowerCase();
    if(r.contains("factura")) return "factura";
    if(r.contains("notacredito")) return "notaCredito";
    if(r.contains("notadebito")) return "notaDebito";
    if(r.contains("guiaremision")) return "guiaRemision";
    if(r.contains("comprobanteretencion") || r.contains("retencion")) return "comprobanteRetencion";
    if(r.contains("liquidacioncompra") || r.contains("liquidacion")) return "liquidacionCompra";
    return null;
  }

  public static String findStringParam(java.lang.reflect.Parameter[] params, Object[] args, String name){
    for(int i=0;i<params.length;i++){
      if(params[i].getName().equals(name) && args[i] instanceof String s) return s;
    }
    return null;
  }

  public static byte[] toBytes(Document doc){
    try{
      javax.xml.transform.Transformer t = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
      java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
      t.transform(new javax.xml.transform.dom.DOMSource(doc), new javax.xml.transform.stream.StreamResult(out));
      return out.toByteArray();
    }catch(Exception e){ throw new RuntimeException(e); }
  }
}
