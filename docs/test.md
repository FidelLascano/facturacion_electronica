# Testing

- **Unit**: services and rules (RBAC, license, XML normalization).
- **Integration**: Testcontainers (Postgres/Redis/MinIO). WireMock for SRI.
- **E2E**: Playwright for the React app (login, list, DLQ, webhooks).

CI will run `mvn -q -DskipTests package` and `pnpm build` to validate build.
