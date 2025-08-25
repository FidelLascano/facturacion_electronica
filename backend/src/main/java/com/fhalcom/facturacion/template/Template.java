
package com.fhalcom.facturacion.template;
import jakarta.persistence.*;
import org.hibernate.annotations.FilterDef; import org.hibernate.annotations.ParamDef; import org.hibernate.annotations.Filter;
import java.time.OffsetDateTime;
@Entity @Table(name="jrxml_templates", uniqueConstraints=@UniqueConstraint(columnNames={"tenant_id","doc_type"}))
@FilterDef(name="tenantFilter", parameters=@ParamDef(name="tenantId", type=org.hibernate.type.StringType.class))
@Filter(name="tenantFilter", condition="tenant_id = :tenantId")
public class Template {
  @Id @GeneratedValue(strategy=GenerationType.UUID) private String id;
  @Column(name="tenant_id", nullable=false, length=64) private String tenantId;
  @Column(name="doc_type", nullable=false, length=32) private String docType;
  @Lob @Column(name="jrxml", nullable=false) private String jrxml;
  @Column(name="updated_at", nullable=false) private OffsetDateTime updatedAt = OffsetDateTime.now();
  public String getId(){return id;}
  public String getTenantId(){return tenantId;} public void setTenantId(String t){this.tenantId=t;}
  public String getDocType(){return docType;} public void setDocType(String d){this.docType=d;}
  public String getJrxml(){return jrxml;} public void setJrxml(String j){this.jrxml=j;}
  public OffsetDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(OffsetDateTime u){this.updatedAt=u;}
}
