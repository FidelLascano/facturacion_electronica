package com.fhalcom.facturacion.xsd;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;

public class ValidationUtil {
  public static void validateXmlBytes(String tenant, String docType, String version, byte[] xml, PreValidationPipeline pipeline){
    try{
      var dbf = DocumentBuilderFactory.newInstance(); dbf.setNamespaceAware(true);
      Document dom = dbf.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(xml));
      pipeline.validate(tenant, docType, version, dom);
    }catch(Exception e){ throw new RuntimeException("XSD validation failed: "+e.getMessage(), e); }
  }
}
