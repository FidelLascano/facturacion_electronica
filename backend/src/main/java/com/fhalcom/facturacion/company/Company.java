
package com.fhalcom.facturacion.company;
import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="company")
public class Company {
  @Id public UUID id;
  public UUID tenant_id;
  public String ruc;
  public String razon_social;
}
