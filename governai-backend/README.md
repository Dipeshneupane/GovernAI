# GovernAI backend

GovernAI is a human-in-the-loop governance decision-support backend. It keeps policy and regulatory evidence attached to every AI-assisted decision so that recommendations remain reviewable and auditable.

## Architecture

The Spring Boot application is a modular monolith using PostgreSQL, Flyway, Spring Data JPA, Jakarta Validation, and Spring Security in development permit-all mode.

```text
Organization + internal policy sections + regulatory sections
                         |
                 deterministic retrieval
                         |
                retrieved evidence records
                         |
      RecommendationGenerator (mock/local by default)
                         |
           recommendation + human-review routing
                         |
                  approval + audit event
```

The schema in `src/main/resources/db/migration/V1__initial_schema.sql` is authoritative. Hibernate runs with `ddl-auto=validate`; it never creates or changes tables.

## Prerequisites and configuration

- Java 21
- PostgreSQL 14+ running locally
- A database named `governai` and a user matching the datasource settings

The default local datasource is configured in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/governai
spring.datasource.username=governai_user
spring.datasource.password=devd
```

For shared or production environments, override these properties with environment variables or external configuration. Do not commit production credentials.

## Run and test

```bash
./mvnw spring-boot:run
./mvnw test
```

Flyway validates and applies migrations at startup. The API starts on `http://localhost:8080`.

## API overview

- `/api/organizations` — organization profile CRUD
- `/api/users`, `/api/roles` — users and predefined reviewer roles
- `/api/policy-documents`, `/api/policy-sections` — internal governance material
- `/api/regulatory-documents`, `/api/regulatory-sections` — external sources
- `/api/decisions` — governance requests, manual evidence, recommendations, approvals
- `/api/decisions/{id}/analyze` — retrieve evidence and create an evidence-grounded development recommendation
- `/api/audit-events/decision/{id}` — append-only decision history

## End-to-end example

1. Create an organization with `POST /api/organizations`.
2. Create an active policy document and its policy sections.
3. Create an active regulatory document and its regulatory sections.
4. Submit a question with `POST /api/decisions`, including the organization ID and jurisdiction.
5. Call `POST /api/decisions/{id}/analyze`.
6. The backend retrieves active internal and external sections, persists them as `retrieved_evidence`, and generates a recommendation using only those evidence IDs.
7. High-risk or insufficient-evidence results become `PENDING_REVIEW`.
8. Submit `POST /api/decisions/{id}/approvals` using `APPROVE`, `EDIT`, `REJECT`, or `ESCALATE`.
9. Read `GET /api/audit-events/decision/{id}` for the trace.

## Recommendation providers

`MockRecommendationGenerator` is the default deterministic, local implementation. It receives structured persisted evidence and returns only those evidence IDs as citations. It is deliberately behind the `RecommendationGenerator` interface so a configured provider implementation can later be substituted without changing controllers or persistence code. No API key is required to start the backend.

## Security status

The development security chain currently permits requests. The user/role model is present, but authentication is intentionally deferred because the schema contains no password or token columns. Do not expose this development configuration publicly.
