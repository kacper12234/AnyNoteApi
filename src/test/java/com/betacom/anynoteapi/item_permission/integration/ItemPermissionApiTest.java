package com.betacom.anynoteapi.item_permission.integration;

import com.betacom.anynoteapi.auth.dto.LoginRequest;
import com.betacom.anynoteapi.item_permission.ItemPermissionRole;
import com.betacom.anynoteapi.item_permission.dto.ItemPermissionRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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

import java.util.UUID;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(scripts = {"classpath:sql/users.sql", "classpath:sql/items.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class ItemPermissionApiTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.4.8");

    @LocalServerPort
    int port;

    private static final String USER_ID = "5ede6a40-f37f-4b50-83d3-9a75c7330f0a";
    private static final String USER_ID_2 = "5ede6a40-f37f-4b50-83d3-9a75c7330f0b";
    private static final String ITEM_ID = "5ede6a40-f37f-4b50-83d3-9a75c7330f0c";
    private static final String ITEM_ID_2 = "5ede6a40-f37f-4b50-83d3-9a75c7330f0d";
    private String token;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        token = loginUser();
    }

    private String loginUser() {
        return given()
                .contentType(ContentType.JSON)
                .body(new LoginRequest("testUser", "test123"))
                .post("/login")
                .then()
                .extract().response().path("token");
    }

    @Test
    void shouldUpdateOnlyToOtherUserAndDeleteOwnItemPermission() {

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new ItemPermissionRequest(UUID.fromString(USER_ID), ItemPermissionRole.VIEWER))
                .when()
                .post("/items/"+ITEM_ID+"/share")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new ItemPermissionRequest(UUID.fromString(USER_ID_2), ItemPermissionRole.VIEWER))
        .when()
                .post("/items/"+ITEM_ID+"/share")
        .then()
                .body("userId", Matchers.equalTo(USER_ID_2))
                .body("itemId", Matchers.equalTo(ITEM_ID))
                .body("role", Matchers.equalTo(ItemPermissionRole.VIEWER.toString()))
                .statusCode(HttpStatus.CREATED.value());

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(new ItemPermissionRequest(UUID.fromString(USER_ID_2), ItemPermissionRole.EDITOR))
                .when()
                .post("/items/"+ITEM_ID+"/share")
                .then()
                .body("userId", Matchers.equalTo(USER_ID_2))
                .body("itemId", Matchers.equalTo(ITEM_ID))
                .body("role", Matchers.equalTo(ItemPermissionRole.EDITOR.toString()))
                .statusCode(HttpStatus.OK.value());

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .delete("/items/"+ITEM_ID_2+ "/share/" + USER_ID_2)
        .then()
                .statusCode(HttpStatus.FORBIDDEN.value());

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/items/"+ITEM_ID+ "/share/" + USER_ID_2)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

}
