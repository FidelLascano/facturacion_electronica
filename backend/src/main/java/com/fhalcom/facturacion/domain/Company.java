
package com.fhalcom.facturacion.domain;
import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="company")
public class Company {
  @Id @GeneratedValue(strategy=GenerationType.AUTO)
  public UUID id;
  @Column(nullable=false, unique=true) public String ruc;
  @Column(nullable=false) public String razonSocial;
  public String certKey; // s3 key: certs/{companyId}.p12
}
