# Config Server

Centralizes externalized configuration for FounderLink services.

Runs on port `8888` and currently uses the classpath-native configuration repository at `src/main/resources/config-repo`.

```bash
mvn -pl config-server spring-boot:run
```

