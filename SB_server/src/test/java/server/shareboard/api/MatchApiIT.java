package server.shareboard.api;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Black-box API tests for MatchController: GET /api/matches/recent.
 */
public class MatchApiIT extends ApiTestSupport {

    @Test
    public void getRecent_defaultLimit_returns200() {
        given()
        .when()
            .get("/api/matches/recent")
        .then()
            .statusCode(200)
            .body("size()", lessThanOrEqualTo(10));
    }

    @Test
    public void getRecent_customLimit_returns200AndRespectsLimit() {
        given()
            .queryParam("limit", 3)
        .when()
            .get("/api/matches/recent")
        .then()
            .statusCode(200)
            .body("size()", lessThanOrEqualTo(3));
    }

    @Test
    public void getRecent_limitAboveMax_isClampedTo50() {
        // Service clamps limit to the 1-50 range rather than erroring,
        // so this should still succeed (not 400) with at most 50 items.
        given()
            .queryParam("limit", 500)
        .when()
            .get("/api/matches/recent")
        .then()
            .statusCode(200)
            .body("size()", lessThanOrEqualTo(50));
    }

    @Test
    public void getRecent_limitBelowMin_isClampedTo1() {
        given()
            .queryParam("limit", 0)
        .when()
            .get("/api/matches/recent")
        .then()
            .statusCode(200)
            .body("size()", lessThanOrEqualTo(1));
    }
}
