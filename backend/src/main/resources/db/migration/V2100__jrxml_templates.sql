
CREATE TABLE IF NOT EXISTS jrxml_templates (
  id           VARCHAR(36) PRIMARY KEY,
  tenant_id    VARCHAR(64) NOT NULL,
  doc_type     VARCHAR(32) NOT NULL,
  jrxml        TEXT NOT NULL,
  updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
  UNIQUE(tenant_id, doc_type)
);
