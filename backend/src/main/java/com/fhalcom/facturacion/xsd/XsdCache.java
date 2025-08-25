
package com.fhalcom.facturacion.xsd;
import org.springframework.cache.annotation.Cacheable; import org.springframework.stereotype.Component;
@Component
public class XsdCache {
  @Cacheable("xsdIndex")
  public javax.xml.validation.Schema loadSchema(String docType, String version){
    try{
      String path = "/xsd/"+docType+"/"+version+".xsd";
      javax.xml.validation.SchemaFactory f = javax.xml.validation.SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
      try (java.io.InputStream in = getClass().getResourceAsStream(path)) {
        if(in==null) throw new IllegalArgumentException("XSD not found: "+path);
        return f.newSchema(new org.xml.sax.InputSource(in).getSystemId()==null ? new java.io.File(path) : null);
      }
    }catch(Exception e){ throw new RuntimeException(e); }
  }
}
