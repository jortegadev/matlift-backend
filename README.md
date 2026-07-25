# MATlift Backend API

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

2. `application.yml` points at `jdbc:postgresql://localhost:5432/matlift_db` and reads credentials from environment variables:
```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
```

3. Run the application. Flyway applies the schema migrations from `src/main/resources/db/migration` automatically on startup:
```bash
mvn spring-boot:run
```

## Testing

The project features a well-balanced testing pyramid to ensure code reliability and maintainability:

*   **Domain Tests:** Pure, blazing-fast unit tests for core business logic.
*   **Use Case Tests:** Leveraging `Mockito` to isolate the application layer.
*   **Web Integration Tests:** `@WebMvcTest` to simulate HTTP requests and validate REST responses.
*   **Persistence Tests:** `@DataJpaTest` using an in-memory database (H2) to validate entity mapping and repository operations.

To run the entire test suite:
```bash
mvn test
```

## API Documentation

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

**Response (201 Created):**
```json
{
    "id": "a89da16b-f4c6-4b75-8c2b-5a95623a2ead",
    "internalLoad": 720
}
```

**Error responses:**

| Status | Cause | Body |
| --- | --- | --- |
| 400 Bad Request | Missing required field | `{"error": "Validation failed", "fields": {"rpe": "is required"}}` |
| 400 Bad Request | Business rule violated | `{"error": "RPE must be between 1 and 10"}` |
| 404 Not Found | `userId` does not exist | `{"error": "User <id> does not exist"}` |
| 409 Conflict | Database constraint violated | `{"error": "Request violates a data integrity constraint"}` |