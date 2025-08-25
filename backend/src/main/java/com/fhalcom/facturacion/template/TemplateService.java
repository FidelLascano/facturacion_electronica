
package com.fhalcom.facturacion.template;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class TemplateService {
  private final TemplateRepository repo;
  public TemplateService(TemplateRepository repo){ this.repo=repo; }
  @Cacheable(cacheNames="jrxmlByTenantDoc", key="#tenantId + ':' + #docType")
  public String getJrxml(String tenantId, String docType){
    return repo.findByTenantIdAndDocType(tenantId, docType).map(Template::getJrxml).orElse(null);
  }
  @Transactional @CacheEvict(cacheNames="jrxmlByTenantDoc", key="#tenantId + ':' + #docType")
  public void save(String tenantId, String docType, String jrxml){
    Template t = repo.findByTenantIdAndDocType(tenantId, docType).orElseGet(Template::new);
    t.setTenantId(tenantId); t.setDocType(docType); t.setJrxml(jrxml); t.setUpdatedAt(java.time.OffsetDateTime.now());
    repo.save(t);
  }
}
