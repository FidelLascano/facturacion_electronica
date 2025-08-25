CREATE TABLE IF NOT EXISTS ride_template (id uuid PRIMARY KEY, tenant_id text NOT NULL, doc_type text NOT NULL, jrxml text NOT NULL, updated_at timestamptz default now(), UNIQUE(tenant_id, doc_type));
CREATE TABLE IF NOT EXISTS ride_branding (id uuid PRIMARY KEY, tenant_id text UNIQUE NOT NULL, logo bytea NOT NULL, content_type text, updated_at timestamptz default now());
