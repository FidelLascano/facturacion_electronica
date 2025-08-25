package com.fhalcom.facturacion.xsd;
import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;

public class XsdValidator {
  public static void validate(Document doc, String classpathXsdPath) {
    try{
      String base = classpathXsdPath.substring(0, classpathXsdPath.lastIndexOf('/')+1);
      var sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      sf.setResourceResolver(new ClassPathResourceResolver(base));
      try (var in = XsdValidator.class.getResourceAsStream(classpathXsdPath)) {
        if(in==null) throw new RuntimeException("XSD not found: " + classpathXsdPath);
        Schema schema = sf.newSchema(new javax.xml.transform.stream.StreamSource(in));
        Validator v = schema.newValidator();
        v.validate(new DOMSource(doc));
      }
    }catch(Exception e){
      throw new RuntimeException("XSD validation error for "+classpathXsdPath+": "+e.getMessage(), e);
    }
  }
}
