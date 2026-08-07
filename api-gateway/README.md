# API Gateway

The edge entry point for FounderLink APIs. It discovers downstream services through Eureka; no routes are defined until business services are introduced.

Runs on port `8080`.

```bash
mvn -pl api-gateway spring-boot:run
```

