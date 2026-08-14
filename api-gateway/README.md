# API Gateway

The edge entry point for FounderLink APIs. It imports infrastructure configuration from Config Server, discovers downstream services through Eureka, and has no routes until business services are introduced.

Runs on port `8080`.

```bash
mvn -pl api-gateway spring-boot:run
```

Set `CONFIG_SERVER_URL` and `EUREKA_DEFAULT_ZONE` when the infrastructure services are not running on their local default URLs.
