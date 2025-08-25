package com.fhalcom.facturacion.domain;
import jakarta.persistence.*;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Filter;
import java.time.OffsetDateTime;

@Entity @Table(name="edoc")
@FilterDef(name="tenantFilter", parameters=@ParamDef(name="tenantId", type=org.hibernate.type.StringType.class))
@Filter(name="tenantFilter", condition="tenant_id = :tenantId")
public class EDoc {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
  public Long id;
  @Column(nullable=false) public String tipo; // FACTURA, NOTA_CREDITO, NOTA_DEBITO, GUIA_REMISION, RETENCION, LIQUIDACION
  @Column(nullable=false) public String version;
  @Column(nullable=false, unique=true) public String claveAcceso;
  @Column(nullable=false) public String estado; // CREATED, SIGNED, RECEIVED, AUTHORIZED, REJECTED
  public String xmlPath; public String pdfPath; public String numeroAutorizacion;
  public OffsetDateTime createdAt = OffsetDateTime.now();
}
