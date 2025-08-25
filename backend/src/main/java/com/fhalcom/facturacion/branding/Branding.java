
package com.fhalcom.facturacion.branding;
import jakarta.persistence.*; import java.time.OffsetDateTime;
import org.hibernate.annotations.FilterDef; import org.hibernate.annotations.ParamDef; import org.hibernate.annotations.Filter;
@Entity @Table(name="branding", uniqueConstraints=@UniqueConstraint(columnNames={"tenant_id"}))
@FilterDef(name="tenantFilter", parameters=@ParamDef(name="tenantId", type=org.hibernate.type.StringType.class))
@Filter(name="tenantFilter", condition="tenant_id = :tenantId")
public class Branding {
  @Id @GeneratedValue(strategy=GenerationType.UUID) private String id;
  @Column(name="tenant_id", nullable=false, unique=true, length=64) private String tenantId;
  @Lob @Column(name="logo") private byte[] logo;
  @Column(name="content_type") private String contentType;
  @Column(name="updated_at", nullable=false) private OffsetDateTime updatedAt = OffsetDateTime.now();
  public String getId(){return id;}
  public String getTenantId(){return tenantId;} public void setTenantId(String t){this.tenantId=t;}
  public byte[] getLogo(){return logo;} public void setLogo(byte[] l){this.logo=l;}
  public String getContentType(){return contentType;} public void setContentType(String c){this.contentType=c;}
  public OffsetDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(OffsetDateTime u){this.updatedAt=u;}
}
