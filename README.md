This project is a backend service for managing notes (called items) with support for sharing, versioning, and full audit history.

The application is built with:

Java 21
Spring Boot 3
Hibernate / JPA
Hibernate Envers (for audit history)
Bucket4j (rate limiting)
JJWT
Lombok
Mapstruct
RestAssured
Mockito
MySQL
Flyway
Testcontainers


The system allows users to:

create and manage their own notes
share notes with other users with different roles (VIEWER / EDITOR)
track full history of changes
show notes available to the logged-in user (either created by them or shared by other users)

Architecture

The codebase is organized by feature (e.g. user, item, auth) instead of traditional layered structure (controller/service/repository globally) to:
- keep related logic close together (controller + service + domain + repository in one module)
- make it easier to navigate and scale when new features are added
- reduce coupling between unrelated parts of the system
- reflect real business domains instead of technical layers

Bandwidth throttling is implemented using a Spring filter because it runs before the controller logic, it does not interfere with the business logic, and it can be easily applied at various endpoints.

Limits are defined in application.properties, e.g.:

rate-limit: </br>
&emsp;rules: </br>
&emsp;&emsp;- path: /login </br>
&emsp;&emsp;capacity: 5 </br>
&emsp;&emsp;duration: 60

I created small abstraction layer wraps Envers queries to avoid leaking Envers API into business logic, change implementation later, map results into DTOs, improve readability and testability.

This project uses Spring Boot Docker Compose support, so the database is started automatically by the application. Containers are stopped automatically when the application shuts down. Docker must be installed.
