
package com.fhalcom.facturacion.iam;
import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="app_user")
public class User {
  @Id public UUID id;
  public UUID tenant_id;
  public String email;
  public String password_hash;
  public Boolean active = true;
}
