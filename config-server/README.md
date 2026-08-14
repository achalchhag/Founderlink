# Config Server

Centralizes externalized configuration for FounderLink services.

Runs on port `8888` and currently uses the classpath-native configuration repository at `src/main/resources/config-repo`. Set `SERVER_PORT` to override its HTTP port and `CONFIG_REPOSITORY_LOCATION` to override the native repository location.

```bash
mvn -pl config-server spring-boot:run
```

The API Gateway configuration is available at `GET /api-gateway/default`.

