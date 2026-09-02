# Profile Service

Profile Service owns FounderLink user profile data. It is independent from Auth Service: Auth Service remains the source of truth for account identity and signs access tokens, while Profile Service stores profile data keyed by the token subject UUID.

## Technology

- Java 21, Spring Boot 3.4.5, Spring Cloud 2024.0.1
- Spring Web, Validation, Data JPA, Flyway, PostgreSQL
- Eureka client and Config Server client
- Spring Security OAuth2 Resource Server with RS256 JWT validation
- springdoc OpenAPI

## Architecture and database

The service is registered as `PROFILE-SERVICE` with Eureka and is exposed locally on port `8082`. Its dedicated PostgreSQL database is `founderlink_profile`; it does not have foreign keys to Auth Service's database. Flyway migration `V1__create_profiles_table.sql` creates the `profiles` table.

```
controller -> dto -> service -> repository -> entity
```

The primary packages are `config`, `controller`, `dto`, `entity`, `exception`, `mapper`, `repository`, and `service`.

## Configuration

Runtime configuration is served by Config Server from `config-server/src/main/resources/config-repo/profile-service.yml`.

| Variable | Purpose |
| --- | --- |
| `DB_URL` | Profile PostgreSQL JDBC URL |
| `DB_USERNAME` / `DB_PASSWORD` | Profile PostgreSQL credentials |
| `PROFILE_DB_NAME` | Compose database name (default: `founderlink_profile`) |
| `AUTH_JWT_ISSUER` | Required issuer for Auth Service access tokens |
| `AUTH_JWT_PUBLIC_KEY` | Base64 X.509 RSA public key used to validate RS256 tokens |
| `CONFIG_SERVER_URL` | Config Server URL |
| `EUREKA_DEFAULT_ZONE` | Eureka URL |

Only the public key is configured here. The private signing key is never provided to Profile Service.

## API

All `/api/profiles/**` endpoints require `Authorization: Bearer <access-token>`.

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/profiles` | Create the authenticated user's profile (201) |
| GET | `/api/profiles/me` | Get the authenticated user's profile |
| PUT | `/api/profiles/me` | Update the authenticated user's profile |
| DELETE | `/api/profiles/me` | Delete the authenticated user's profile (204) |
| GET | `/api/profiles/{userId}` | Read a profile by Auth Service user UUID |

`userId` is never accepted in create/update requests. It is taken from JWT `sub`, preventing profile impersonation.

Example create request:

```json
{
  "headline": "Founder building SaaS products",
  "bio": "Building products and connecting with ambitious founders.",
  "location": "Mumbai, India",
  "skills": "Java, Spring Boot, PostgreSQL, SaaS",
  "experienceYears": 3,
  "websiteUrl": "https://example.com",
  "linkedinUrl": "https://linkedin.com/in/example",
  "githubUrl": "https://github.com/example"
}
```

Successful responses contain profile fields and timestamps, never passwords, tokens, or authentication credentials. Validation failures return 400, missing profiles 404, duplicate profiles 409, unauthenticated requests 401, and forbidden requests 403.

## Run

Build and test:

```powershell
mvn -pl profile-service -am verify
```

Run the full stack with Docker after supplying the required database and JWT environment variables:

```powershell
docker compose up --build
```

Swagger UI is available at `http://localhost:8082/swagger-ui.html`; Profile Service registers in Eureka at `http://localhost:8761` and is routed through Gateway at `http://localhost:8080/api/profiles/**`.
