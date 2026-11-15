# triviaquestionservice

A Java 21 Spring Boot REST API built with Maven for the [Quad Solutions assignment](https://www.quad.team/assignment).

The application retrieves trivia questions from the [Open Trivia Database](https://opentdb.com/) and provides an endpoint to verify submitted answers without directly exposing the correct answers.

## Requirements

- Java 21
- Maven (or the included wrapper: `./mvnw`)

## Running the application

```bash
export APP_HASH_SECRET="<a long random string>"
mvn spring-boot:run
```

The service listens on port `8080` by default.

### Running with Docker

```bash
docker build -t triviaquestionservice .
docker run --rm -p 8080:8080 -e APP_HASH_SECRET="<a long random string>" triviaquestionservice
```

Any setting from the configuration table below can be passed with `-e`. The secret is not stored in the image, so always provide `APP_HASH_SECRET` when starting the container.

### Running with Docker Compose

```bash
docker compose up --build
```

`compose.yaml` builds the image, publishes port `8080` and passes the settings through. To override anything, copy `.env.example` to `.env` and uncomment the lines you want. Keep `.env` out of version control.

`APP_HASH_SECRET` is optional here to make local testing easy, but without it the public development secret is used. Set your own secret for anything shared or deployed.

## Testing the application

```bash
mvn clean test
```

## Configuration

Settings are read from `src/main/resources/application.properties` and can be overridden with environment variables.

| Environment variable | Default | Description |
|---|---|---|
| `SPRING_APPLICATION_NAME` | `triviaquestionservice` | Application name. |
| `SERVER_PORT` | `8080` | HTTP port. |
| `SPRINGDOC_API_DOCS_PATH` | `/v3/api-docs` | OpenAPI JSON path. |
| `SPRINGDOC_SWAGGER_UI_PATH` | `/swagger-ui.html` | Swagger UI path. |
| `LOGGING_LEVEL_ROOT` | `INFO` | Root log level. |
| `LOGGING_LEVEL_APP` | `DEBUG` | Log level for `nl.bramv.triviaquestionservice`. |
| `TRIVIA_PROVIDER` | `opentriviadb` | Questions provider. |
| `OPENTDB_PROVIDER_NAME` | `OpenTriviaDB` | Provider name used in logs and error messages. |
| `OPENTDB_BASE_URL` | `https://opentdb.com` | Open Trivia Database base URL. |
| `OPENTDB_CONNECT_TIMEOUT` | `2s` | Connect timeout for calls to Open Trivia Database. |
| `OPENTDB_READ_TIMEOUT` | `5s` | Read timeout for calls to Open Trivia Database. |
| `VERIFICATION_TYPE` | `hashVerification` | Answer verification strategy. |
| `APP_HASH_SECRET` | `development-only-secret` | Secret key used to sign and verify answer tokens. **Always set this yourself** (see warning below). |

> **Warning:** if `APP_HASH_SECRET` is not set, the application falls back to the default `development-only-secret`. That value is public, so anyone can forge valid answer tokens against an instance that uses it. Never run a shared or production instance without setting your own secret.

## API

Interactive documentation is generated with OpenAPI and available through [Swagger UI](http://localhost:8080/swagger-ui/index.html) while the application is running.

### `GET /questions`

Returns trivia questions. The correct answer is **not** included; each question carries a `verificationToken` instead.

| Parameter | Required | Description |
|---|---|---|
| `amount` | no (default `1`) | Number of questions, 1 to 50. |
| `category` | no | Open Trivia Database category ID. |
| `difficulty` | no | `EASY`, `MEDIUM` or `HARD`. |
| `type` | no | `MULTIPLE` or `BOOLEAN`. |
| `token` | no | Open Trivia Database session token. Accepted and passed through, but not yet managed by this service. |

Example response:

```json
[
  {
    "type": "MULTIPLE",
    "difficulty": "EASY",
    "category": "Geography",
    "question": "What is the capital of France?",
    "verificationToken": "…",
    "answers": ["Berlin", "Paris", "Madrid", "Rome"]
  }
]
```

Answers are shuffled on every request.

### `POST /checkanswers`

Checks a submitted answer against the question's verification token.

```json
{
  "question": "What is the capital of France?",
  "selectedAnswer": "Paris",
  "verificationToken": "…"
}
```

Response:

```json
{ "correct": true }
```

The `question` must be sent exactly as it was returned by `/questions`. All three fields are required.

### Error responses

Errors are returned as RFC 9457 `ProblemDetail` JSON.

| Status | Meaning |
|---|---|
| `400` | Invalid or missing request fields, or a value the provider rejected (for example an unknown category or an invalid session token). |
| `404` | No questions found for the given filters. |
| `429` | Open Trivia Database rate limit reached. |
| `502` | Open Trivia Database returned an error or unusable data. |
| `503` | Open Trivia Database could not be reached, timed out, or is unavailable. |

## Question/Answer verification

The question and correct answer are used to generate an HMAC-SHA256 verification token with a server-side secret key. When an answer is submitted, the service generates the token for the submitted question and answer and compares it with the supplied token using a constant-time comparison.

This keeps the API stateless and horizontally scalable without directly exposing correct answers.

Known limitations:

- **Answers can be guessed.** Multiple choice questions have only four options, and booleans have two, so a client can simply try each one. Stateless verification cannot prevent this.
- **Tokens do not expire and can be replayed.** A token stays valid for as long as the secret stays the same.
- **Rotating the secret invalidates all previously issued tokens.**

## Architecture

```
controller      HTTP endpoints and request validation
service         Application logic, answer verification, response mapping
service/provider  IQuestionsProvider and the Open Trivia Database implementation
exception       Provider exceptions, HTTP mapping and logging
```

The questions provider and answer verification strategy are both behind interfaces (`IQuestionsProvider`, `IAnswerVerificationService`) and selected through configuration, so alternative implementations can be added without touching the controllers.