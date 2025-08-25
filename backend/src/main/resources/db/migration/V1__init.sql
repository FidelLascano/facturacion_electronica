
-- UUID generator default via application code
create extension if not exists "uuid-ossp";

create table if not exists tenant(
  id uuid primary key,
  key varchar(64) unique not null,
  name varchar(200) not null,
  created_at timestamptz not null default now()
);

create table if not exists app_group(
  id uuid primary key,
  tenant_id uuid not null references tenant(id),
  name varchar(150) not null,
  created_at timestamptz not null default now()
);
create table if not exists app_group_closure(
  ancestor uuid not null references app_group(id),
  descendant uuid not null references app_group(id),
  depth int not null,
  primary key(ancestor, descendant)
);

create table if not exists app_user(
  id uuid primary key,
  tenant_id uuid not null references tenant(id),
  email varchar(255) not null,
  password_hash varchar(255) not null,
  mfa_secret varchar(64),
  active boolean not null default true,
  created_at timestamptz not null default now(),
  unique(tenant_id,email)
);

create table if not exists role(
  id uuid primary key,
  tenant_id uuid not null references tenant(id),
  name varchar(80) not null,
  unique(tenant_id, name)
);
create table if not exists permission(
  id uuid primary key,
  code varchar(120) unique not null,
  description varchar(255)
);
create table if not exists role_permission(
  role_id uuid not null references role(id),
  permission_id uuid not null references permission(id),
  primary key(role_id, permission_id)
);
create table if not exists user_group_role(
  user_id uuid not null references app_user(id),
  group_id uuid not null references app_group(id),
  role_id uuid not null references role(id),
  primary key(user_id, group_id, role_id)
);

create table if not exists company(
  id uuid primary key,
  tenant_id uuid not null references tenant(id),
  ruc varchar(13) not null,
  razon_social varchar(200) not null,
  created_at timestamptz not null default now(),
  unique(tenant_id, ruc)
);
create table if not exists company_group(
  company_id uuid not null references company(id),
  group_id uuid not null references app_group(id),
  primary key(company_id, group_id)
);
create table if not exists company_email(
  id uuid primary key,
  company_id uuid not null references company(id),
  email varchar(255) not null,
  label varchar(50),
  primary boolean not null default false
);
create table if not exists license(
  id uuid primary key,
  company_id uuid not null references company(id),
  status varchar(20) not null,
  valid_from date not null,
  valid_to date not null
);
create table if not exists license_event(
  id uuid primary key,
  license_id uuid not null references license(id),
  event_type varchar(40) not null,
  created_at timestamptz not null default now()
);
create table if not exists company_certificate(
  id uuid primary key,
  company_id uuid not null references company(id),
  s3_key varchar(400) not null,
  created_at timestamptz not null default now()
);

create table if not exists einvoice_document(
  id uuid primary key,
  tenant_id uuid not null references tenant(id),
  company_id uuid not null references company(id),
  ruc varchar(13) not null,
  secuencial varchar(20) not null,
  clave_acceso varchar(60) not null,
  estado varchar(20) not null default 'CREATED',
  sri_num_autorizacion varchar(50),
  xml_s3_key varchar(400),
  ride_s3_key varchar(400),
  created_at timestamptz not null default now()
);

create table if not exists ingestion_dedup(
  id uuid primary key,
  hash varchar(64) not null,
  ruc varchar(13) not null,
  secuencial varchar(20) not null,
  created_at timestamptz not null default now(),
  unique(hash)
);
create table if not exists dead_letter(
  id uuid primary key,
  tenant_id uuid not null references tenant(id),
  connector varchar(60) not null,
  payload jsonb not null,
  error text not null,
  retries int not null default 0,
  created_at timestamptz not null default now()
);

create table if not exists webhook_subscription(
  id uuid primary key,
  tenant_id uuid not null references tenant(id),
  url varchar(400) not null,
  secret varchar(120) not null,
  active boolean not null default true,
  created_at timestamptz not null default now()
);
create table if not exists webhook_delivery(
  id uuid primary key,
  subscription_id uuid not null references webhook_subscription(id),
  event_code varchar(80) not null,
  payload jsonb not null,
  status varchar(20) not null,
  attempts int not null,
  created_at timestamptz not null default now()
);

create table if not exists audit_event(
  id uuid primary key,
  tenant_id uuid not null references tenant(id),
  user_id uuid,
  action varchar(120) not null,
  entity varchar(120),
  entity_id uuid,
  at timestamptz not null default now(),
  details jsonb
);

-- dev seed
insert into tenant(id,key,name) values
  ('00000000-0000-0000-0000-000000000001','demo','Tenant Demo')
on conflict do nothing;

insert into app_group(id,tenant_id,name) values
  ('00000000-0000-0000-0000-000000000010','00000000-0000-0000-0000-000000000001','ROOT')
on conflict do nothing;

insert into app_user(id,tenant_id,email,password_hash,active) values
  ('00000000-0000-0000-0000-000000000100','00000000-0000-0000-0000-000000000001','admin@local',
   '$2a$10$Kj5XmmYH7Wz4C7.z1LDRFukw3oQh/2w79x3Z1S6m6Xg6fKkZb8n3C', true) -- password: admin
on conflict do nothing;

insert into role(id,tenant_id,name) values
  ('00000000-0000-0000-0000-000000000200','00000000-0000-0000-0000-000000000001','ADMIN')
on conflict do nothing;

insert into permission(id,code,description) values
  ('00000000-0000-0000-0000-000000000300','EINVOICE.WRITE','Create and manage E-Invoices')
on conflict do nothing;

insert into role_permission(role_id,permission_id) values
  ('00000000-0000-0000-0000-000000000200','00000000-0000-0000-0000-000000000300')
on conflict do nothing;

insert into user_group_role(user_id,group_id,role_id) values
  ('00000000-0000-0000-0000-000000000100','00000000-0000-0000-0000-000000000010','00000000-0000-0000-0000-000000000200')
on conflict do nothing;


insert into permission(id,code,description) values
  ('00000000-0000-0000-0000-000000000301','ADMIN.MANAGE','Admin endpoints')
on conflict do nothing;

insert into role_permission(role_id,permission_id) values
  ('00000000-0000-0000-0000-000000000200','00000000-0000-0000-0000-000000000301')
on conflict do nothing;
