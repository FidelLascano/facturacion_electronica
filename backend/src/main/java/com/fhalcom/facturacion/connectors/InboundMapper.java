package com.fhalcom.facturacion.connectors;
import com.fasterxml.jackson.databind.*;
import java.util.*;

public class InboundMapper {
  static ObjectMapper M = new ObjectMapper();
  public static class Mapping {
    public String docType; public String template; public Map<String,String> fields;
  }
  public static String render(String mappingJson, Map<String,Object> row){
    try{
      Mapping m = M.readValue(mappingJson, Mapping.class);
      String xml = m.template;
      for(var e: row.entrySet()){
        String k = e.getKey();
        String v = e.getValue()==null? "" : String.valueOf(e.getValue());
        xml = xml.replace("${"+k+"}", escape(v));
      }
      return xml;
    }catch(Exception e){ throw new RuntimeException(e); }
  }
  static String escape(String s){
    return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace(""","&quot;").replace("'","&apos;");
  }
}
