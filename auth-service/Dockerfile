FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./pom.xml
COPY config-server/pom.xml config-server/pom.xml
COPY discovery-server/pom.xml discovery-server/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
RUN mvn --batch-mode -pl auth-service -am dependency:go-offline
COPY auth-service/src auth-service/src
RUN mvn --batch-mode -pl auth-service -am package -DskipTests
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN apt-get update && apt-get install --yes --no-install-recommends wget && rm -rf /var/lib/apt/lists/* && groupadd --system spring && useradd --system --gid spring spring
COPY --from=build /workspace/auth-service/target/auth-service-0.0.1-SNAPSHOT.jar app.jar
USER spring:spring
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
