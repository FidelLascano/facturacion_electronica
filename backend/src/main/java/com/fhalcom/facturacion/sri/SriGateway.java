
package com.fhalcom.facturacion.sri;
import okhttp3.*;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SriGateway {
  private final String recepcionUrl, autorizacionUrl;
  private final OkHttpClient client = new OkHttpClient();
  private static final String NS_REC = "http://ec.gob.sri.ws.recepcion";
  private static final String NS_AUT = "http://ec.gob.sri.ws.autorizacion";

  public SriGateway(@Value("${app.sri.recepcionUrl}") String r, @Value("${app.sri.autorizacionUrl}") String a){
    this.recepcionUrl = r; this.autorizacionUrl = a;
  }

  public boolean recepcion(String xml){
    String envelope = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:rec='"+NS_REC+"'>"
      + "<soapenv:Header/>"
      + "<soapenv:Body><rec:validarComprobante><xml>"+escape(xml)+"</xml></rec:validarComprobante></soapenv:Body>"
      + "</soapenv:Envelope>";
    try{
      Request req = new Request.Builder().url(recepcionUrl)
        .post(RequestBody.create(envelope.getBytes(), MediaType.parse("text/xml; charset=utf-8")))
        .addHeader("SOAPAction", "validarComprobante").build();
      try(Response res = client.newCall(req).execute()){
        String body = res.body().string();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
          .parse(new java.io.ByteArrayInputStream(body.getBytes()));
        String status = textContent(doc, "estado");
        return res.isSuccessful() && status != null && !status.equalsIgnoreCase("DEVUELTA");
      }
    }catch(Exception e){ return false; }
  }

  public String autorizacion(String clave){
    String envelope = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:aut='"+NS_AUT+"'>"
      + "<soapenv:Header/>"
      + "<soapenv:Body><aut:autorizacionComprobante><claveAccesoComprobante>"+clave+"</claveAccesoComprobante></aut:autorizacionComprobante></soapenv:Body>"
      + "</soapenv:Envelope>";
    try{
      Request req = new Request.Builder().url(autorizacionUrl)
        .post(RequestBody.create(envelope.getBytes(), MediaType.parse("text/xml; charset=utf-8")))
        .addHeader("SOAPAction", "autorizacionComprobante").build();
      try(Response res = client.newCall(req).execute()){
        String body = res.body().string();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
          .parse(new java.io.ByteArrayInputStream(body.getBytes()));
        String estado = textContent(doc, "estado");
        String num = textContent(doc, "numeroAutorizacion");
        if("AUTORIZADO".equalsIgnoreCase(estado) && num!=null && !num.isBlank()) return num;
      }
    }catch(Exception e){}
    return null;
  }

  private String escape(String s){ return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;"); }
  private String textContent(Document doc, String tag){
    var nodes = doc.getElementsByTagName(tag);
    return nodes.getLength()>0 ? nodes.item(0).getTextContent() : null;
  }
}
