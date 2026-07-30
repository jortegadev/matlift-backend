# MatLift Backend API

A RESTful API developed in Java and Spring Boot for workout and event management. This project is strictly designed following **Clean Architecture** and **Domain-Driven Design (DDD)** principles.

Currently, the domain core focuses on sports performance tracking, allowing users to log training sessions (BJJ, strength, contact sports) and automatically calculating the internal load based on the session's duration and RPE (Rating of Perceived Exertion).

## Tech Stack

*   **Language:** Java 21+
*   **Framework:** Spring Boot 4
*   **Database:** PostgreSQL
*   **Persistence:** Spring Data JPA / Hibernate
*   **Migrations:** Flyway
*   **Mapping:** MapStruct
*   **Testing:** JUnit 5, Mockito, AssertJ, H2 (In-memory DB)
*   **Dependency Manager:** Maven

## Architecture

The project utilizes a package structure based on Clean/Hexagonal Architecture, completely isolating pure business rules from technical details (frameworks, databases, and UI).

## Features (MVP)

*   **Workout Logging:** REST endpoint to create training sessions detailing category, duration, and RPE.
*   **Automatic Load Calculation:** The domain automatically calculates the internal load (`RPE * minutes`) upon creation.
*   **Domain Validations:** Pure business rules (e.g., ensuring RPE is strictly within a 1-10 scale) protected against invalid states.
*   **Global Exception Handling:** Translates domain-specific exceptions into clear HTTP responses (400 Bad Request, 404 Not Found, 409 Conflict) using `@RestControllerAdvice`.

## Installation & Setup

### Prerequisites
*   Java JDK 21 or higher.
*   PostgreSQL installed and running.
*   Maven.

### Steps

1. Clone the repository:
```bash
git clone https://github.com/jortegadev/matlift-backend.git
```

2. `application.yml` points at `jdbc:postgresql://localhost:5432/matlift_db` and reads credentials and the JWT signing secret from environment variables:
```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=at-least-32-bytes-long-random-secret
```

`JWT_SECRET` has no default on purpose — a signing secret committed to the repository would let anyone forge tokens. It must be at least 32 bytes, as required by HMAC-SHA256, and the application refuses to start otherwise.

3. Run the application. Flyway applies the schema migrations from `src/main/resources/db/migration` automatically on startup:
```bash
mvn spring-boot:run
```

## Testing

The project features a well-balanced testing pyramid to ensure code reliability and maintainability:

*   **Domain Tests:** Pure, blazing-fast unit tests for core business logic.
*   **Use Case Tests:** Leveraging `Mockito` to isolate the application layer.
*   **Web Integration Tests:** `@WebMvcTest` to simulate HTTP requests and validate REST responses.
*   **Persistence Tests:** `@DataJpaTest` backed by a real PostgreSQL container (Testcontainers), running the actual Flyway migrations with `ddl-auto: validate` — the same schema and dialect as production.

Running the suite requires a local Docker daemon (Docker Desktop or equivalent). All database-backed tests share a single PostgreSQL container for the whole run via `AbstractIntegrationTest`.

To run the entire test suite:
```bash
mvn test
```

## API Documentation

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/users` | Register a user |
| `POST` | `/api/workouts` | Create a workout session |
| `GET` | `/api/workouts/{id}` | Fetch a single session |
| `GET` | `/api/workouts` | List a user's sessions, paginated and newest first |
| `PUT` | `/api/workouts/{id}` | Replace a session's data |
| `DELETE` | `/api/workouts/{id}` | Delete a session |

### Register User
`POST /api/users`

**Request Body:**
```json
{
    "email": "atleta@matlift.com",
    "password": "supersecret"
}
```

Passwords must be at least 8 characters and are stored as a BCrypt hash — never in plain text, and never returned by any endpoint. Emails are normalized to lowercase, so `Atleta@MatLift.com` and `atleta@matlift.com` are the same account.

**Response (201 Created):**
```json
{
    "id": "b98a966c-8214-4c29-8cb5-567865768541",
    "email": "atleta@matlift.com"
}
```

Returns `409 Conflict` if the email is already registered.

### Create Workout Session
`POST /api/workouts`

**Request Body:**
```json
{
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "sessionDate": "2026-07-24T18:30:00Z",
    "category": "CONTACT_SPORT",
    "activityName": "BJJ Gi",
    "durationMinutes": 90,
    "rpe": 8,
    "notes": "Guard passing focused session."
}
```

**Response (201 Created):** the full resource representation, same shape returned by `GET` and `PUT`.
```json
{
    "id": "a89da16b-f4c6-4b75-8c2b-5a95623a2ead",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "sessionDate": "2026-07-24T18:30:00Z",
    "category": "CONTACT_SPORT",
    "activityName": "BJJ Gi",
    "durationMinutes": 90,
    "rpe": 8,
    "internalLoad": 720,
    "notes": "Guard passing focused session."
}
```

### List Workout Sessions
`GET /api/workouts?userId={uuid}&from={iso}&to={iso}&page=0&size=20`

`userId` is required; `from` and `to` are optional and may be used independently. Results are ordered by `sessionDate` descending.

```json
{
    "content": [ { "id": "...", "internalLoad": 720 } ],
    "page": 0,
    "size": 20,
    "totalElements": 3,
    "totalPages": 1
}
```

### Update Workout Session
`PUT /api/workouts/{id}`

Same body as `POST` but **without** `userId` — a session's owner never changes. `internalLoad` is recalculated from the new duration and RPE.

### Delete Workout Session
`DELETE /api/workouts/{id}` — returns `204 No Content`, or `404` if the session does not exist.

**Error responses:**

| Status | Cause | Body |
| --- | --- | --- |
| 400 Bad Request | Missing required field | `{"error": "Validation failed", "fields": {"rpe": "is required"}}` |
| 400 Bad Request | Business rule violated | `{"error": "RPE must be between 1 and 10"}` |
| 404 Not Found | `userId` does not exist | `{"error": "User <id> does not exist"}` |
| 409 Conflict | Database constraint violated | `{"error": "Request violates a data integrity constraint"}` |