package com.betacom.anynoteapi.item.integration;

import com.betacom.anynoteapi.auth.dto.LoginRequest;
import com.betacom.anynoteapi.item.dto.CreateItemRequest;
import com.betacom.anynoteapi.item.dto.UpdateItemRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
@Sql(scripts = "classpath:sql/users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class ItemApiTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.4.8");

    @LocalServerPort
    int port;

    private static final String USER_ID = "5ede6a40-f37f-4b50-83d3-9a75c7330f0a";

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    private String loginUser(LoginRequest loginRequest) {
        return given()
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .post("/login")
                .then()
                    .extract().response().path("token");
    }

    @Test
    void shouldReturnAllRevisions_whenItemIsUpdatedMultipleTimes() {
        String token = loginUser(new LoginRequest("testUser", "test123"));

        Response createItemResponse = given()
                                            .contentType(ContentType.JSON)
                                            .header("Authorization", "Bearer " + token)
                                            .body(new CreateItemRequest("Test Item", "Test Description"))
                                            .post("/items")
                                    .then()
                                            .statusCode(HttpStatus.CREATED.value())
                                            .body("id", Matchers.notNullValue())
                                            .body("ownerId", Matchers.equalTo(USER_ID))
                                            .body("title", Matchers.equalTo("Test Item"))
                                            .body("content", Matchers.equalTo("Test Description"))
                                            .body("createdAt", Matchers.notNullValue())
                                            .body("updatedAt", Matchers.notNullValue())
                                    .extract().response();

        String itemId = createItemResponse.path("id");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UpdateItemRequest("Updated Title", null, 0))
                .patch("/items/" + itemId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.equalTo(itemId))
                .body("title", Matchers.equalTo("Updated Title"))
                .body("content", Matchers.equalTo("Test Description"))
                .body("version", Matchers.equalTo(1))
                .body("updatedAt", Matchers.notNullValue());

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new UpdateItemRequest(null, "Updated Description", 1))
                .patch("/items/" + itemId)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.equalTo(itemId))
                .body("title", Matchers.equalTo("Updated Title"))
                .body("content", Matchers.equalTo("Updated Description"))
                .body("version", Matchers.equalTo(2))
                .body("updatedAt", Matchers.notNullValue());

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .get("/items/" + itemId + "/history")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.equalTo(3))
                .body("revision", Matchers.contains(1, 2, 3))
                .body("revisionType", Matchers.contains(
                        "ADD",
                        "MOD",
                        "MOD"
                ))
                .body("title", Matchers.contains(
                        "Test Item",
                        "Updated Title",
                        "Updated Title"

                ))
                .body("content", Matchers.contains(
                        "Test Description",
                        "Test Description",
                        "Updated Description"
                ))
                .body("timestamp", Matchers.everyItem(Matchers.notNullValue()))
                .body("changedBy", Matchers.everyItem(Matchers.equalTo("testUser")));
    }

    @Test
    @Sql(scripts = {"classpath:sql/items.sql", "classpath:sql/permissions.sql"})
    void shouldViewAvailableItems() {
        String token = loginUser(new LoginRequest("testUser2", "test123"));

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .get("/items")
        .then()
                .body("size()", Matchers.equalTo(2))
                .body("id", Matchers.containsInAnyOrder("5ede6a40-f37f-4b50-83d3-9a75c7330f0c", "5ede6a40-f37f-4b50-83d3-9a75c7330f0d"))
                .body("ownerId", Matchers.containsInAnyOrder(USER_ID, "5ede6a40-f37f-4b50-83d3-9a75c7330f0b"))
                .body("title", Matchers.everyItem(Matchers.equalTo("test")))
                .body("content", Matchers.everyItem(Matchers.equalTo("test")))
                .body("version", Matchers.everyItem(Matchers.equalTo(0)))
                .body("updatedAt", Matchers.everyItem(Matchers.equalTo("2020-01-01T00:00:00Z")))
                .body("myRole", Matchers.containsInAnyOrder("VIEWER", "OWNER"))
                .statusCode(HttpStatus.OK.value());
    }
}
