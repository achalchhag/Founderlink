# Discovery Service

Discovery Service provides authenticated discovery of FounderLink profiles. It runs on port `8083` and has no database: Profile Service remains the sole owner of profile data.

## Responsibilities and communication

The service registers with Eureka as `DISCOVERY-SERVICE` and uses OpenFeign with `PROFILE-SERVICE` service discovery/load balancing. It forwards the incoming `Authorization: Bearer ...` header to Profile Service; tokens are never logged. Discovery Service is an OAuth2 Resource Server and validates JWTs using `AUTH_JWT_ISSUER` and `AUTH_JWT_PUBLIC_KEY`.

Profile Service exposes the authenticated search operation used here, so no direct PostgreSQL access occurs.

## Endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/discovery/founders?location=&skill=&headline=` | Search profiles using optional filters. |
| GET | `/api/discovery/founders/{userId}` | Get a profile by Auth Service user ID. |

Swagger UI is at `/swagger-ui.html`.

## Run

Set `AUTH_JWT_ISSUER`, `AUTH_JWT_PUBLIC_KEY`, `DB_USERNAME`, and `DB_PASSWORD`, then run `docker compose up --build`. Config Server and Eureka are dependencies configured in Compose.

For a local run, start Config Server, Eureka, and Profile Service, then run:

```powershell
mvn -pl discovery-service spring-boot:run
```

Examples (replace the token and UUID):

```powershell
curl -H "Authorization: Bearer <token>" "http://localhost:8080/api/discovery/founders?location=Mumbai&skill=Java"
curl -H "Authorization: Bearer <token>" "http://localhost:8080/api/discovery/founders/<userId>"
```
