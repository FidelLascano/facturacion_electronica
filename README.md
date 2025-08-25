# Facturación Electrónica Ecuador — SaaS (Modular Monolith)

Production‑grade implementation scaffold based on the provided SUPER PROMPT.
This repository includes:

- **backend/** Java 21 + Spring Boot 3
- **frontend/** React 18 + TypeScript (Vite)
- **infra/** Docker Compose (dev) and Helm chart (prod)
- **docs/** Detailed guides for dev/demo/live/testing
- **.github/** GitHub Actions CI
- **.gitlab-ci.yml** GitLab CI

> Timezone default: `America/Guayaquil`

## Quick start (Dev)

```bash
# 1) Copy env example and adjust if needed
cp infra/.env.example .env

# 2) Start stack (first time may take a while)
docker compose -f infra/docker-compose.yml --env-file .env up -d --build

# 3) Seed dev admin: admin@local / admin (via Flyway)
# 4) Open:
#    - Backend API: http://localhost:8080/actuator/health
#    - Frontend UI: http://localhost:5173
#    - MinIO:       http://localhost:9001 (console), http://localhost:9000 (S3)
#    - Redis:       redis://localhost:6379
#    - Postgres:    postgres://localhost:5432/facturacion
#    - MailHog:     http://localhost:8025
#    - Vault (dev): http://localhost:8200 (root token from .env)
#    - WireMock:    http://localhost:9999/__admin
```

## Bounded Contexts

- IAM/Tenancy, Companies, Licenses/Certs, E‑Docs, Ingestion, Webhooks, Branding, Observability

## Endpoints (excerpt)

- `POST /auth/login` (JWT + optional TOTP)  
- `GET /me/permissions`  
- `POST /einvoice` → create & sign  
- `POST /einvoice/{id}/send`  
- `POST /einvoice/{id}/authorize`  
- `GET /einvoice/{id}/download?type=xml|ride`  
- `POST /admin/cache/invalidate`  
- `GET /branding`  
- `POST /webhooks/subscriptions` … `GET /webhooks/deliveries`

See **docs/api.md** for the full list.


---

## Enterprise Additions in v1.1
- JWT **Bearer filter** + `PermissionEvaluator` (RBAC desde BD con `@PreAuthorize`).
- **Rate limiting** (Bucket4j, por tenant) + **Cuotas diarias** (Redis).
- Firma **XML-DSig** (enveloped) usando certificado desde MinIO; PIN vía Vault (modo dev simulado).
- **SRI SOAP** (recepción/autorización) con parseo básico (WireMock en dev).
- **RIDE PDF** (openhtmltopdf) guardado en S3.
- **Webhooks** con **HMAC** y reintentos programados (DLQ si falla).
- **Auditoría** via Aspect.


## Enterprise v2.0 Additions
- **Admin APIs & UI**: compañías (carga de certificados .p12 a MinIO), webhooks (alta/listado), auditoría (listado).
- **Descargas**: `/einvoice/{id}/download?type=xml|ride` saca archivos desde S3/MinIO.
- **Observabilidad**: Micrometer + **/actuator/prometheus**, **Prometheus+Grafana** en docker-compose.
- **Helm**: Ingress TLS, HPA, ConfigMaps/Secrets, anotaciones de **Vault Agent** listas.
- **Firma XAdES-BES (light)**: modo `xades` simulado sobre XML-DSig con `QualifyingProperties` mínimo (dev); estructura preparada para librería real.


## v3.0 — One-shot enterprise pack
- **XAdES-BES** (con `xades4j`) integrado (modo `xades` real).
- **Tracing**: Micrometer Tracing + OTLP exporter listo para OpenTelemetry Collector.
- **RBAC reforzado**: permisos `ADMIN.MANAGE` en endpoints de admin.
- **Docs & listing**: endpoint para listar documentos y descargar XML/ride desde UI.
- **CI**: build de Docker + **CycloneDX SBOM** en backend.


## v4.0 — Enterprise Final (no placeholders)
- HMAC SHA-256 real para webhooks.
- KMS Vault KV v2 real (OkHttp) para PIN de certificados cuando `APP_KMS_MODE=vault`.
- Parser SRI SOAP robusto (Recepción / Autorización) por XML.
- RIDE enriquecido (datos de emisor/receptor/totales).
- Semillas: compañía DEMO precreada, certificado referenciable.
- Descargas XML/RIDE desde S3 confirmadas.


## v5.0 — Integración SRI oficial (namespaces, WSDL, XSD hooks)
- **Namespaces SRI** oficiales en SOAP: recepción `http://ec.gob.sri.ws.recepcion`, autorización `http://ec.gob.sri.ws.autorizacion`.
- **Validación XSD** opcional (activable con `APP_VALIDATE_XSD=true`), con mapeo por tipo de comprobante y versión.
- **Ambientes**: `celcer.sri.gob.ec` (certificación) y `cel.sri.gob.ec` (producción) parametrizados en `.env`.


## v6.0 — Enterprise All-in (SUPER PROMPT)
- **MFA TOTP**: alta y verificación (`/mfa/setup`, `/mfa/verify`).
- **RBAC**: permisos `ADMIN.MANAGE`, `EINVOICE.READ`, `EINVOICE.WRITE` con `@PreAuthorize`.
- **Rate limit** por tenant vía header `X-Tenant-ID`.
- **OpenTelemetry Collector** en Compose (OTLP 4317/4318).
- **Frontend**: Tailwind + React Query.
- **Playwright/Vitest** scaffolding.
- Mantiene **SRI oficial**, **XSD hooks**, **firma XAdES lista** y almacenamiento S3.
- Repositorio sin TODOs ni placeholders: todo el código incluido compila con dependencias.

### Descarga de XSD oficiales
Ejecuta `scripts/xsd-sync.sh` para sincronizar XSD públicos (ATS/REOC/devIva) y espejos confiables de esquemas XML de comprobantes.


## v7.0 — ALL COMPLETE
- Tipos soportados: **Factura**, **Nota de Crédito**, **Nota de Débito**, **Guía**, **Retención**, **Liquidación**; versiones: 1.1.0/2.0.0/2.1.0 según tipo.
- **XSDs embebidos** (directorio `resources/xsd/vendor`) para validación inmediata sin dependencias externas.
- **RIDE Jasper** para factura; fácilmente extensible a otros tipos.
- Endpoints unificados `/edoc/create`, `/edoc/{id}/xml`, `/edoc/{id}/ride`.


## v8.0 — Enterprise TOTAL
- Firma (XAdES-BES/XMLDSig) con certificado P12 desde S3 (MinIO), PIN por Vault.
- Flujo completo: **crear+firmar** → **enviar (Recepción SRI)** → **autorizar (Autorización SRI)** con **RIDE** y **webhooks HMAC** al autorizar.
- **S3** para XML/RIDE, **Redis** para cuotas, **Actuator** para Prometheus.
- **Admin UI**: compañías (cargar .p12), webhooks, listado de documentos.


## v9.0 — Enterprise MAX
- **RIDE por tipo** (Factura, NC, ND, Guía, Retención, Liquidación) con **ítems**.
- **Exportación ZIP** masiva de XML/PDF desde `/edoc/export`.
- **Auditoría**: endpoint `/audit` con purge.
- **Frontend**: vistas **Audit** y **Export**.
- **Helm chart** completo (Deployment/Service/Ingress/Secret).
- **CI**: CodeQL SAST, Trivy, OWASP Dependency-Check, build FE/BE.


# Enterprise Additions
- RIDE: QR configurable, templates por tenant/tipo, branding, preview endpoint, samples.
- Signature: XAdES-BES with pluggable key provider (LocalKeystoreProvider).
- Connectors JDBC: polling + mapping + ingest, dead-letter, Prometheus metrics, admin testing.
- TenantConfig API.
- Audit hash-chain + verify endpoint.



### XSD dinámico con política por tenant
- **Auto-escaneo** de `classpath:/xsd/vendor/sri/**.xsd` al iniciar (no dependemos del `index.json`).
- `XsdVersionResolver` aplica política por tenant desde `/tenant-config`:
  ```json
  {
    "xsd": {
      "policy": "latest",
      "pinned": { "factura": "2.1.0", "comprobanteRetencion": "2.0.0" }
    }
  }
  ```
- `latest` si no hay versión solicitada ni pin por tenant.
- **Resolver de imports** (`ClassPathResourceResolver`) soporta `xsd:import/include` dentro del vendor.
- **Tests de contrato** (`XsdContractTest`): valida samples por tipo contra el XSD **latest** disponible.


### Validador global (Filter)
- Activación: `app.validation.filter.enabled=true` (por defecto).
- Aplica a requests `Content-Type: application/xml` en cualquier endpoint.
- Determina `docType` desde header `X-Doc-Type` o desde la raíz del XML.
- Valida usando la política del tenant y añade cabeceras de respuesta:
  - `X-XSD-DocType`, `X-XSD-Version`, `X-XSD-Path`.
- En error responde `400` con JSON: `{ "error": "XSD validation failed", "detail": "..." }`.

