package server.shareboard.api;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Black-box API tests for AuthController: POST /api/auth/register, POST /api/auth/login.
 */
public class AuthApiIT extends ApiTestSupport {

    // A known-good user created once in @BeforeClass, reused by the login tests.
    private String knownUsername;
    private final String knownPassword = "correctHorseBattery"; // 8+ chars required by UserService

    @BeforeClass(alwaysRun = true)
    public void registerKnownUser() {
        knownUsername = uniqueUsername("qa_login_user");

        given()
            .contentType("application/json")
            .body("""
                {
                  "username": "%s",
                  "passwordHash": "%s",
                  "organization": false
                }
                """.formatted(knownUsername, knownPassword))
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201);
    }

    // ---------- register ----------

    @Test
    public void register_happyPath_returns201WithUserIdAndUsername() {
        String username = uniqueUsername("qa_new_user");

        given()
            .contentType("application/json")
            .body("""
                {
                  "username": "%s",
                  "passwordHash": "aLongEnoughPassword",
                  "organization": true
                }
                """.formatted(username))
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .body("username", equalTo(username))
            .body("userId", greaterThan(0));
    }

    @Test
    public void register_emptyUsernameAndPassword_returns400() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "username": "",
                  "passwordHash": "",
                  "organization": true
                }
                """)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400);
    }

    @Test
    public void register_shortPassword_returns400() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "username": "%s",
                  "passwordHash": "short",
                  "organization": false
                }
                """.formatted(uniqueUsername("qa_short_pw")))
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400);
    }

    @Test
    public void register_duplicateUsername_returns400() {
        // knownUsername already exists from @BeforeClass
        given()
            .contentType("application/json")
            .body("""
                {
                  "username": "%s",
                  "passwordHash": "anotherValidPassword",
                  "organization": false
                }
                """.formatted(knownUsername))
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(400);
    }

    // ---------- login ----------

    @Test
    public void login_happyPath_returns200WithUserIdAndUsername() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(knownUsername, knownPassword))
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
            .body("username", equalTo(knownUsername))
            .body("userId", greaterThan(0));
    }

    @Test
    public void login_wrongPassword_returns400() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "username": "%s",
                  "password": "definitelyWrongPassword"
                }
                """.formatted(knownUsername))
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(400);
    }

    @Test
    public void login_nonexistentUser_returns400() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "username": "user_that_does_not_exist_12345",
                  "password": "whateverPassword"
                }
                """)
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(400);
    }

    @Test
    public void login_emptyCredentials_returns400() {
        given()
            .contentType("application/json")
            .body("""
                {
                  "username": "",
                  "password": ""
                }
                """)
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(400);
    }
}
