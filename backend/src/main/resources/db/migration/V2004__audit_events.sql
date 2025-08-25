CREATE TABLE IF NOT EXISTS audit_events (id uuid PRIMARY KEY, tenant_id text, actor text, action text, resource text, details text, prev_hash text, hash text, at timestamptz default now());
