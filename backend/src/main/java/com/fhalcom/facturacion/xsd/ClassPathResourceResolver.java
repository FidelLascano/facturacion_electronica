package com.fhalcom.facturacion.xsd;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

public class ClassPathResourceResolver implements LSResourceResolver {
  private final String baseDir;
  public ClassPathResourceResolver(String baseDir){ this.baseDir = baseDir.endsWith("/")? baseDir : baseDir + "/"; }
  @Override public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
    try{
      String path = systemId;
      if(!path.startsWith("/")) path = baseDir + path;
      InputStream in = getClass().getResourceAsStream(path);
      if(in==null) return null;
      return new LSInput() {
        public Reader getCharacterStream(){ return null; }
        public void setCharacterStream(Reader characterStream){}
        public InputStream getByteStream(){ return in; }
        public void setByteStream(InputStream byteStream){}
        public String getStringData(){ try { return new String(in.readAllBytes()); } catch(Exception e){ return null; } }
        public void setStringData(String stringData){}
        public String getSystemId(){ return systemId; }
        public void setSystemId(String systemId){}
        public String getPublicId(){ return publicId; }
        public void setPublicId(String publicId){}
        public String getBaseURI(){ return baseURI; }
        public void setBaseURI(String baseURI){}
        public String getEncoding(){ return "UTF-8"; }
        public void setEncoding(String encoding){}
        public boolean getCertifiedText(){ return false; }
        public void setCertifiedText(boolean certifiedText){}
      };
    }catch(Exception e){ return null; }
  }
}
