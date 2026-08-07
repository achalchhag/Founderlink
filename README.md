# FounderLink

FounderLink is an AI-powered startup co-founder matchmaking platform, built as a Java 21 and Spring Boot microservices monorepo.

## Repository layout

- `config-server` — centralized externalized configuration.
- `discovery-server` — Eureka service discovery.
- `api-gateway` — edge routing entry point.
- `auth-service` — planned authentication and authorization service.
- `profile-service` — planned user profile service.
- `startup-service` — planned startup idea service.
- `matching-service` — planned collaborator matching service.
- `chat-service` — planned real-time chat service.
- `notification-service` — planned notification service.
- `docs` — architecture and operational documentation.

## Prerequisites

- Java 21
- Maven 3.9+
- Docker and Docker Compose (when containers are added)

## Current infrastructure services

Start the services in this order:

```bash
mvn -pl config-server spring-boot:run
mvn -pl discovery-server spring-boot:run
mvn -pl api-gateway spring-boot:run
```

The Config Server runs on `8888`, Eureka on `8761`, and the API Gateway on `8080`.

## Build

```bash
mvn clean verify
```

Business-service modules will be added incrementally.

