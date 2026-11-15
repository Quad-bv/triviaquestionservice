# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Resolve dependencies first so this layer is cached until pom.xml changes.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
# Tests run in CI (`mvn package`), so they are skipped here to keep image builds fast.
RUN mvn -B -q package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Run as an unprivileged user.
RUN groupadd --system app && useradd --system --gid app app

COPY --from=build /build/target/*.jar app.jar
USER app

EXPOSE 8080

# Size the heap from the container memory limit. Override with -e JAVA_OPTS="...".
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

# The secret is deliberately NOT baked into the image: pass APP_HASH_SECRET at run time.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]