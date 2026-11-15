# triviaquestionservice

A Java 21 Spring Boot REST API built with Maven for the [Quad Solutions assignment](https://www.quad.team/assignment).

The application retrieves trivia questions from the [Open Trivia Database](https://opentdb.com/) and provides an endpoint to verify submitted answers without directly exposing the correct answers.

## Running the application

```bash
mvn spring-boot:run
```

## Testing the application

```bash
mvn clean test
```

## Configuration

The application uses default configuration values that can be overridden through environment variables.

A verification secret is required to generate and validate answer tokens. Set `VERIFICATION_SECRET` before starting the application.

See `src/main/resources/application.properties` for available settings.

## API Documentation

API documentation is generated using OpenAPI and is available through [Swagger UI](http://localhost:8080/swagger-ui/index.html) when the application is running.

## Question/Answer verification

The correct answer is used to generate an HMAC-SHA256 verification token with a secret key. Submitted answers are verified by generating and comparing the corresponding token.

This keeps the API stateless and horizontally scalable without directly exposing correct answers. However, the limited number of possible answers means they can still be guessed.