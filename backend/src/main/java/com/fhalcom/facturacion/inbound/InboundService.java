package com.fhalcom.facturacion.inbound;
import org.springframework.stereotype.Service;
import com.fhalcom.facturacion.storage.S3Storage;
import com.fhalcom.facturacion.repo.EDocRepository;
import com.fhalcom.facturacion.domain.EDoc;
@Service
public class InboundService {
  private final com.facturacion.xsd.PreValidationPipeline preval;
  private final S3Storage s3; private final EDocRepository edocs;
  public InboundService(S3Storage s3, EDocRepository edocs, com.facturacion.xsd.PreValidationPipeline preval){ this.s3=s3; this.edocs=edocs; this.preval=preval; }
  public Long ingest(String tenant, String docType, String xml){
    try{
      var dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance(); dbf.setNamespaceAware(true);
      org.w3c.dom.Document dom = dbf.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(xml.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
      preval.validate(tenant, docType, null, dom);
    }catch(Exception ex){ throw new RuntimeException("XSD validation failed: "+ex.getMessage(), ex);}
    try{
      String key = "inbound/"+tenant+"/"+docType+"/"+java.util.UUID.randomUUID()+".xml";
      s3.putBytes(key, xml.getBytes(java.nio.charset.StandardCharsets.UTF_8), "application/xml");
      EDoc e = new EDoc(); e.tipo = docType.toUpperCase(); e.xmlPath = key; e.estado = "RECIBIDA"; edocs.save(e);
      return e.id;
    }catch(Exception e){ throw new RuntimeException(e); }
  }
}
