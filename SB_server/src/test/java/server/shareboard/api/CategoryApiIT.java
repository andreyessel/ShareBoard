package server.shareboard.api;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Black-box API tests for CategoryController: GET /api/categories, GET /api/categories/{id}.
 * Assumes the seed data in sql/02-seed.sql (categories 1-6) has been loaded.
 */
public class CategoryApiIT extends ApiTestSupport {

    @Test
    public void findAll_returns200WithSeededCategories() {
        given()
        .when()
            .get("/api/categories")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(6))
            .body("name", hasItem("Blankets"));
    }

    @Test
    public void findById_existingCategory_returns200() {
        given()
            .pathParam("categoryId", 1)
        .when()
            .get("/api/categories/{categoryId}")
        .then()
            .statusCode(200)
            .body("categoryId", equalTo(1))
            .body("name", notNullValue());
    }

    @Test
    public void findById_nonexistentCategory_returns404() {
        given()
            .pathParam("categoryId", 999999)
        .when()
            .get("/api/categories/{categoryId}")
        .then()
            .statusCode(404);
    }
}
