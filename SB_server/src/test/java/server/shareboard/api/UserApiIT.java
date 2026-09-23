package server.shareboard.api;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Black-box API tests for UserController: GET /api/user/{id}, GET /api/user/username/{username}.
 */
public class UserApiIT extends ApiTestSupport {

    private int knownUserId;
    private String knownUsername;

    @BeforeClass(alwaysRun = true)
    public void registerKnownUser() {
        knownUsername = uniqueUsername("qa_user_lookup");

        knownUserId = given()
            .contentType("application/json")
            .body("""
                {
                  "username": "%s",
                  "passwordHash": "aLongEnoughPassword",
                  "organization": false
                }
                """.formatted(knownUsername))
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract().path("userId");
    }

    @Test
    public void findById_existingUser_returns200() {
        given()
            .pathParam("id", knownUserId)
        .when()
            .get("/api/user/{id}")
        .then()
            .statusCode(200)
            .body("userId", equalTo(knownUserId))
            .body("username", equalTo(knownUsername));
    }

    @Test
    public void findById_invalidId_returns400() {
        given()
            .pathParam("id", -1)
        .when()
            .get("/api/user/{id}")
        .then()
            .statusCode(400);
    }

    @Test
    public void findById_nonexistentId_returns404() {
        given()
            .pathParam("id", 9_999_999)
        .when()
            .get("/api/user/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    public void findByUsername_existingUser_returns200() {
        given()
            .pathParam("username", knownUsername)
        .when()
            .get("/api/user/username/{username}")
        .then()
            .statusCode(200)
            .body("userId", equalTo(knownUserId))
            .body("username", equalTo(knownUsername));
    }

    @Test
    public void findByUsername_nonexistentUser_returns404() {
        given()
            .pathParam("username", "no_such_user_qa_12345")
        .when()
            .get("/api/user/username/{username}")
        .then()
            .statusCode(404);
    }
}
