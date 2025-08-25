package com.fhalcom.facturacion.ride;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import com.fhalcom.facturacion.domain.EDoc;
@Component
public class RideQrBuilder {
  private final String template;
  public RideQrBuilder(@Value("${ride.qr.template:https://srienlinea.sri.gob.ec/consulta?claveAcceso=${claveAcceso}}") String t){ this.template=t; }
  public String build(EDoc e){
    String out = template;
    if(e.claveAcceso!=null) out = out.replace("${claveAcceso}", e.claveAcceso);
    if(e.numeroAutorizacion!=null) out = out.replace("${numeroAutorizacion}", e.numeroAutorizacion);
    return out;
  }
}
