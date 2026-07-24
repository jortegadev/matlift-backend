# MATlift Backend API

A RESTful API developed in Java and Spring Boot for workout and event management. This project is strictly designed following **Clean Architecture** and **Domain-Driven Design (DDD)** principles.

Currently, the domain core focuses on sports performance tracking, allowing users to log training sessions (BJJ, strength, contact sports) and automatically calculating the internal load based on the session's duration and RPE (Rating of Perceived Exertion).

## Tech Stack

*   **Language:** Java 17+
*   **Framework:** Spring Boot 3
*   **Database:** PostgreSQL
*   **Persistence:** Spring Data JPA / Hibernate
*   **Testing:** JUnit 5, Mockito, AssertJ, H2 (In-memory DB)
*   **Dependency Manager:** Maven

## Architecture

The project utilizes a package structure based on Clean/Hexagonal Architecture, completely isolating pure business rules from technical details (frameworks, databases, and UI).

## Features (MVP)

*   **Workout Logging:** REST endpoint to create training sessions detailing category, duration, and RPE.
*   **Automatic Load Calculation:** The domain automatically calculates the internal load (`RPE * minutes`) upon creation.
*   **Domain Validations:** Pure business rules (e.g., ensuring RPE is strictly within a 1-10 scale) protected against invalid states.
*   **Global Exception Handling:** Translates domain-specific exceptions into clear HTTP responses (400 Bad Request) using `@RestControllerAdvice`.

## Installation & Setup

### Prerequisites
*   Java JDK 17 or higher.
*   PostgreSQL installed and running.
*   Maven.

### Steps

1. Clone the repository:
```bash
git clone [https://github.com/jortegadev/matlift-backend.git](https://github.com/jortegadev/matlift-backend.git)
```

2. Configure the PostgreSQL database. Make sure to set up your credentials in the `application.properties` or `application.yml` file:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/matlift_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Run the application:
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
    "internalLoad": 720,
    "message": "Workout successfully saved"
}
```