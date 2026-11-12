# FounderLink

FounderLink is an AI-powered startup co-founder matchmaking platform, built as a Java 21 and Spring Boot microservices monorepo.

## Repository layout

### Infrastructure services
- `config-server` — centralized externalized configuration (port 8888).
- `discovery-server` — Eureka service discovery (port 8761).
- `api-gateway` — edge routing entry point (port 8080).

### Business services
- `auth-service` — user registration, login, and RS256 JWT authentication (port 8081).
- `profile-service` — user profile CRUD, skill matching, and search (port 8082).
- `discovery-service` — founder profile discovery via Feign (port 8083).
- `startup-service` — startup idea management (port 8084).
- `matching-service` — co-founder & startup matchmaking score calculation (port 8085).
- `chat-service` — messaging and direct conversations (port 8086).
- `notification-service` — event notifications and alerts (port 8087).

### Other
- `docs` — architecture and operational documentation.

## Prerequisites

- Java 21
- Maven 3.9+
- Docker and Docker Compose

## Quick start

### 1. Generate RSA keys and create `.env`

Copy `.env.example` to `.env` and follow the instructions inside to generate RSA key pairs and set database credentials.

### 2. Start with Docker Compose

```bash
docker compose up --build
```

### 3. Start locally (without Docker)

Start services in this order:

```bash
mvn -pl config-server spring-boot:run
mvn -pl discovery-server spring-boot:run
mvn -pl api-gateway spring-boot:run
mvn -pl auth-service spring-boot:run
mvn -pl profile-service spring-boot:run
mvn -pl discovery-service spring-boot:run
mvn -pl startup-service spring-boot:run
mvn -pl matching-service spring-boot:run
mvn -pl chat-service spring-boot:run
mvn -pl notification-service spring-boot:run
```

## API routes (via Gateway on port 8080)

| Route | Target service | Port |
|---|---|---|
| `/api/auth/**` | auth-service | 8081 |
| `/api/profiles/**` | profile-service | 8082 |
| `/api/discovery/**` | discovery-service | 8083 |
| `/api/startups/**` | startup-service | 8084 |
| `/api/matching/**` | matching-service | 8085 |
| `/api/chat/**` | chat-service | 8086 |
| `/api/notifications/**` | notification-service | 8087 |

## Build

```bash
mvn clean verify
```

## Technology stack

- Java 21, Spring Boot 3.4.5, Spring Cloud 2024.0.1
- Spring Web MVC, Spring Data JPA, PostgreSQL, Flyway
- Spring Security with RS256 JWT authentication
- Spring Cloud Config Server, Eureka, Gateway, OpenFeign
- springdoc OpenAPI (Swagger UI on `/swagger-ui.html`)
- Docker and Docker Compose
