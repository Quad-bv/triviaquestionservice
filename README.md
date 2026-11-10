# triviaquestionservice

A Java 21 Spring Boot REST API built with Maven created for the [Quad Solutions assignment](https://www.quad.team/assignment).

The application retrieves trivia questions from [Open Trivia Database](https://opentdb.com/) and provides an endpoint to verify submitted answers without exposing the correct answers directly.

## Running the application

``
mvn spring-boot:run
``

## Testing the application

``
mvn clean test
``

## Configuration
The application uses default configuration values that can be overridden using environment variables.

See `` src/main/resources/application.properties `` for available settings.

## API Documentation

API documentation is generated using OpenAPI and can be accessed through [Swagger UI](http://localhost:8080/swagger-ui/index.html) after starting the application.

## Question/Answer verification

The correct answer is hashed with a secret key and returned as a verification token. Submitted answers are hashed with the same key and compared against the token.

This keeps the API stateless and horizontally scalable, while avoiding exposing the correct answer directly. Hashing is quick and one-way, but answers can still be guessed due to the limited number of possible answers.