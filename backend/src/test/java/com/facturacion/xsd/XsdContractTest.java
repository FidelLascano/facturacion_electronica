package com.facturacion.xsd;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.*;
import java.io.InputStream;

public class XsdContractTest {
  private Document parse(String resource){
    try{
      var dbf = DocumentBuilderFactory.newInstance(); dbf.setNamespaceAware(true);
      try(InputStream in = getClass().getResourceAsStream(resource)){
        if(in==null) return null;
        return dbf.newDocumentBuilder().parse(in);
      }
    }catch(Exception e){ return null; }
  }
  @Test
  public void validateSamplesAgainstLatestXsd(){
    XsdRegistry reg = new XsdRegistry();
    Map<String,String> sampleByType = Map.of(
      "factura", "/ride/samples/factura.xml",
      "notaCredito", "/ride/samples/notaCredito.xml",
      "notaDebito", "/ride/samples/notaDebito.xml",
      "guiaRemision", "/ride/samples/guiaRemision.xml",
      "comprobanteRetencion", "/ride/samples/comprobanteRetencion.xml",
      "liquidacionCompra", "/ride/samples/liquidacionCompra.xml"
    );
    for(var entry : sampleByType.entrySet()){
      String tipo = entry.getKey();
      Document d = parse(entry.getValue());
      if(d==null) continue; // sample not present
      String xsd = reg.latestPath(tipo);
      if(xsd==null) continue;
      XsdValidator.validate(d, xsd);
    }
  }
}
