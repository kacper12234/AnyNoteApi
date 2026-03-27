package com.betacom.anynoteapi.auth.integration;

import com.betacom.anynoteapi.auth.dto.LoginRequest;
import com.betacom.anynoteapi.auth.dto.RegisterRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AuthApiTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.4.8");

    @LocalServerPort
    int port;

    @Value("${jwt.expiration}")
    private Long expiration;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @Sql(scripts = "classpath:sql/users.sql")
    void shouldRegisterLoginAndEnforceRateLimit() {

        for (int i = 0; i < 5; i++) {
            given()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("testUser", "test123"))
            .when()
                    .post("/login")
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("token", Matchers.notNullValue())
                    .body("expiresIn", Matchers.equalTo(expiration.intValue()));
        }

        given()
                .contentType(ContentType.JSON)
                .body(new LoginRequest("testUser", "test123"))
        .when()
                .post("/login")
        .then()
                .statusCode(HttpStatus.TOO_MANY_REQUESTS.value())
                .header("Retry-After", Matchers.notNullValue());
    }

    @Test
    void shouldRegisterOnlyWithProperLoginAndPassword() {
        given()
                .contentType(ContentType.JSON)
                .body(new RegisterRequest("user1", "test123"))
                .when()
                .post("/register")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        given()
                .contentType(ContentType.JSON)
                .body(new RegisterRequest("user1", "Test12345"))
                .when()
                .post("/register")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        given()
                .contentType(ContentType.JSON)
                .body(new RegisterRequest("user1", "Test12345!"))
                .when()
                .post("/register")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .contentType(ContentType.JSON)
                .body(new RegisterRequest("user1", "Test12345!"))
                .when()
                .post("/register")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }
}
