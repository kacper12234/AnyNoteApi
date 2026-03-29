<h5>This project is a backend service for managing notes (called items) with support for sharing, versioning, and full audit history.</h5>

<h5>The application is built with:</h5>

Java 21 <br/>
Spring Boot 3<br/>
Hibernate / JPA<br/>
Hibernate Envers (for audit history)<br/>
Bucket4j (rate limiting)<br/>
JJWT<br/>
Lombok<br/>
Mapstruct<br/>
RestAssured<br/>
Mockito<br/>
MySQL<br/>
Flyway<br/>
Testcontainers<br/>

<h5>The system allows users to:</h5>

- create and manage their own notes <br/>
- share notes with other users with different roles (VIEWER / EDITOR)<br/>
- track full history of changes<br/>
- show notes available to the logged-in user (either created by them or shared by other users)<br/>

<h5>Architecture</h5>

The codebase is organized by feature (e.g. user, item, auth) instead of traditional layered structure (controller/service/repository globally) to:
- keep related logic close together (controller + service + domain + repository in one module)
- make it easier to navigate and scale when new features are added
- reduce coupling between unrelated parts of the system
- reflect real business domains instead of technical layers

I created small abstraction layer wraps <b>Envers</b> queries to avoid leaking Envers API into business logic, make it easier to change the implementation if necessary, and improve the code’s readability and testability.

Request rate limiting is implemented using <b>Bucket4j</b> in a Spring filter because it runs before the controller logic, it does not interfere with the business logic, and it can be easily applied at various endpoints.

Limits are defined in application.properties, e.g.:

rate-limit: </br>
&emsp;rules: </br>
&emsp;&emsp;- path: /login </br>
&emsp;&emsp;capacity: 5 </br>
&emsp;&emsp;duration: 60

path - The URL pattern that the rate limit applies to. It supports wildcard patterns. </br>
capacity - The maximum number of requests allowed within the defined time window. </br>
duration - The time window in seconds during which the capacity is enforced. <br/>
For example, capacity: 5 and duration: 60 means that a maximum of 5 requests are allowed within a 60-second time window.

This project uses Spring Boot Docker Compose support, so the database is started automatically by the application. Containers are stopped automatically when the application shuts down. Docker must be installed.
