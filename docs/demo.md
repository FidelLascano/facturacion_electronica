# Demo Guide

This guide uses **WireMock** to stub SRI endpoints.

1. Ensure containers are up:
   ```bash
   docker compose -f infra/docker-compose.yml --env-file .env up -d
   ```
2. Login (dev seed): `POST /auth/login` with `{"email":"admin@local","password":"admin"}`  
   Copy the `token` returned.
3. Create an invoice:
   ```bash
   curl -H "Authorization: Bearer $TOKEN" -H "X-Tenant-ID: demo"         -H "Content-Type: application/json"         -d @docs/samples/einvoice-min.json         http://localhost:8080/einvoice
   ```
4. Send and authorize, then download the `ride.pdf`:
   ```bash
   curl -H "Authorization: Bearer $TOKEN" -H "X-Tenant-ID: demo"         -X POST http://localhost:8080/einvoice/$ID/send
   curl -H "Authorization: Bearer $TOKEN" -H "X-Tenant-ID: demo"         -X POST http://localhost:8080/einvoice/$ID/authorize
   curl -H "Authorization: Bearer $TOKEN" -H "X-Tenant-ID: demo"         -L "http://localhost:8080/einvoice/$ID/download?type=ride" -o ride.pdf
   ```
