
package com.fhalcom.facturacion.edoc;
import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="einvoice_document")
public class EInvoice {
  @Id public UUID id;
  public UUID tenant_id;
  public UUID company_id;
  public String ruc;
  public String secuencial;
  public String clave_acceso;
  public String estado;
  public String sri_num_autorizacion;
  public String xml_s3_key;
  public String ride_s3_key;
}
