# API (Selected)

## Auth
- `POST /auth/login` → { token, mfaRequired?, totpSetupUri? }
- `GET /me/permissions`

Headers:
- `X-Tenant-ID`: tenant key.
- `X-Group-ID` (optional): group context.

## E‑Invoice
- `POST /einvoice` body: minimal document fields → returns {id}
- `POST /einvoice/{id}/send`
- `POST /einvoice/{id}/authorize`
- `GET /einvoice/{id}/download?type=xml|ride` → streams

## Admin/Branding
- `POST /admin/cache/invalidate`
- `GET /branding`

## Webhooks
- `POST /webhooks/subscriptions`
- `GET /webhooks/deliveries`


### Policies
- **Rate limit**: por tenant (Bucket4j). 429 si excede.
- **Cuota diaria**: `APP_QUOTA_DOCS_PER_DAY`, contador en Redis.

### Webhooks
- Crear suscripciones en DB (tabla `webhook_subscription`). Eventos:
  - `EINVOICE.AUTHORIZED` → payload `{ id, num }`
- Encabezado: `X-Signature` (SHA-256 simulada con secreto).

### Descargas
- RIDE PDF almacenado en S3 bajo `docs/{id}/ride.pdf` tras autorización.
