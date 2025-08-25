
-- MFA TOTP and permissions model
CREATE TABLE IF NOT EXISTS permission (
  id SERIAL PRIMARY KEY,
  code VARCHAR(64) UNIQUE NOT NULL,
  description VARCHAR(255)
);
CREATE TABLE IF NOT EXISTS role_permission (
  role_id BIGINT NOT NULL REFERENCES role(id),
  permission_id BIGINT NOT NULL REFERENCES permission(id),
  PRIMARY KEY(role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS user_mfa (
  user_id BIGINT PRIMARY KEY REFERENCES app_user(id),
  secret VARCHAR(64) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT FALSE
);

-- seed permissions
INSERT INTO permission(code,description) VALUES
 ('ADMIN.MANAGE','Acceso endpoints administrativos'),
 ('EINVOICE.WRITE','Emitir y autorizar documentos'),
 ('EINVOICE.READ','Listar y consultar documentos')
ON CONFLICT (code) DO NOTHING;

-- link ADMIN role to all permissions
INSERT INTO role_permission(role_id, permission_id)
  SELECT 1, id FROM permission ON CONFLICT DO NOTHING;


-- eDoc table (generalized)
CREATE TABLE IF NOT EXISTS edoc (
  id SERIAL PRIMARY KEY,
  tipo VARCHAR(32) NOT NULL,
  version VARCHAR(16) NOT NULL,
  claveAcceso VARCHAR(64) UNIQUE NOT NULL,
  estado VARCHAR(24) NOT NULL,
  xmlPath VARCHAR(255),
  pdfPath VARCHAR(255),
  numeroAutorizacion VARCHAR(64),
  createdAt TIMESTAMPTZ DEFAULT now()
);


CREATE TABLE IF NOT EXISTS company (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ruc VARCHAR(20) UNIQUE NOT NULL,
  razonSocial VARCHAR(255) NOT NULL,
  certKey VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS audit_event (
  id SERIAL PRIMARY KEY,
  action VARCHAR(64),
  entity VARCHAR(64),
  entityId VARCHAR(64),
  at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS webhook_subscription (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  url TEXT NOT NULL,
  secret TEXT NOT NULL,
  active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS webhook_delivery (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  subscriptionId UUID NOT NULL,
  event VARCHAR(64) NOT NULL,
  payload TEXT,
  status VARCHAR(32) DEFAULT 'PENDING',
  attempts INT DEFAULT 0,
  createdAt TIMESTAMPTZ DEFAULT now()
);
