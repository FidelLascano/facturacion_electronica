# Live/Prod Guide

> **Important**: Replace `.env` secrets with Vault/KMS and Kubernetes secrets.  
> Enable HTTPS/TLS and WAF/ratelimits at the Ingress.

- Configure SRI official endpoints in `APP_SRI_*` envs.
- Store company P12/PFX in S3 (MinIO compatible) per tenant/company.
- Put P12 PIN in **Vault** (`secret/data/certs/{companyId}`) and set `APP_KMS_MODE=vault`.
- Set strong `APP_JWT_SECRET`, rotate regularly.
- Enable Redis, Prometheus/Grafana, and OTel collector.
- Configure quotas and rate limits per tenant in env or DB policy.

See **infra/helm/** and **docs/api.md**.


### Certificados
- Cargue su `.p12` a S3/MinIO en `certs/{companyId}.p12` y configure `APP_CERT_OBJECTKEY` si es necesario.
- El PIN se obtiene desde Vault en modo `vault` (dev usa valor fijo).


## Observabilidad
- **Actuator Prometheus** en `/actuator/prometheus`.
- Despliegue Prometheus+Grafana (compose o Helm). Configure dashboards para:
  - Tasa de emisión/autorización por tenant
  - Latencias SRI
  - 429 y cuotas
  - Errores Webhooks/DLQ


## SRI — Endpoints oficiales
- Certificación (testing): `https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline` y `.../AutorizacionComprobantesOffline`
- Producción: `https://cel.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline` y `.../AutorizacionComprobantesOffline`

## XSD oficiales
Copia los XSD del SRI en **backend/src/main/resources/xsd/** según el mapeo de `XsdRegistry` y activa `APP_VALIDATE_XSD=true`.
