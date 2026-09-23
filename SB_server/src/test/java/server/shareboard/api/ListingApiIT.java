package server.shareboard.api;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Black-box API tests for ListingController: create, findById, findAll,
 * myListings, update, delete (withdraw). Uses category id 1 ("Blankets")
 * from the seed data.
 */
public class ListingApiIT extends ApiTestSupport {

    private int testUserId;
    private static final int SEEDED_CATEGORY_ID = 1;

    @BeforeClass(alwaysRun = true)
    public void registerTestUser() {
        String username = uniqueUsername("qa_listing_user");

        testUserId = given()
            .contentType("application/json")
            .body("""
                {
                  "username": "%s",
                  "passwordHash": "aLongEnoughPassword",
                  "organization": false
                }
                """.formatted(username))
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201)
            .extract().path("userId");
    }

    // ---------- create ----------

    @Test
    public void create_needListing_happyPath_returns201() {
        String body = """
            {
              "userId": %d,
              "categoryId": %d,
              "type": "NEED",
              "quantity": 10,
              "urgency": "MEDIUM",
              "neededBy": "%s"
            }
            """.formatted(testUserId, SEEDED_CATEGORY_ID, LocalDate.now().plusDays(14));

        given()
            .contentType("application/json")
            .body(body)
        .when()
            .post("/api/listings")
        .then()
            .statusCode(201)
            .body("userId", equalTo(testUserId))
            .body("status", equalTo("OPEN"))
            .body("quantityRemaining", equalTo(10))
            .body("listingId", greaterThan(0));
    }

    @Test
    public void create_surplusListing_happyPath_returns201() {
        String body = """
            {
              "userId": %d,
              "categoryId": %d,
              "type": "SURPLUS",
              "quantity": 5,
              "availableUntil": "%s"
            }
            """.formatted(testUserId, SEEDED_CATEGORY_ID, LocalDate.now().plusDays(30));

        given()
            .contentType("application/json")
            .body(body)
        .when()
            .post("/api/listings")
        .then()
            .statusCode(201)
            .body("status", equalTo("OPEN"))
            .body("quantityRemaining", equalTo(5));
    }

    @Test
    public void create_needListingMissingNeededBy_returns400() {
        String body = """
            {
              "userId": %d,
              "categoryId": %d,
              "type": "NEED",
              "quantity": 10,
              "urgency": "HIGH"
            }
            """.formatted(testUserId, SEEDED_CATEGORY_ID);

        given()
            .contentType("application/json")
            .body(body)
        .when()
            .post("/api/listings")
        .then()
            .statusCode(400);
    }

    @Test
    public void create_zeroQuantity_returns400() {
        String body = """
            {
              "userId": %d,
              "categoryId": %d,
              "type": "SURPLUS",
              "quantity": 0,
              "availableUntil": "%s"
            }
            """.formatted(testUserId, SEEDED_CATEGORY_ID, LocalDate.now().plusDays(30));

        given()
            .contentType("application/json")
            .body(body)
        .when()
            .post("/api/listings")
        .then()
            .statusCode(400);
    }

    @Test
    public void create_missingCategoryId_returns400() {
        String body = """
            {
              "userId": %d,
              "type": "SURPLUS",
              "quantity": 5,
              "availableUntil": "%s"
            }
            """.formatted(testUserId, LocalDate.now().plusDays(30));

        given()
            .contentType("application/json")
            .body(body)
        .when()
            .post("/api/listings")
        .then()
            .statusCode(400);
    }

    // ---------- findById / findAll / myListings ----------

    @Test
    public void findById_existingListing_returns200() {
        int listingId = createOpenSurplusListing();

        given()
            .pathParam("listingId", listingId)
        .when()
            .get("/api/listings/{listingId}")
        .then()
            .statusCode(200)
            .body("listingId", equalTo(listingId));
    }

    @Test
    public void findById_nonexistentListing_returns404() {
        given()
            .pathParam("listingId", 9_999_999)
        .when()
            .get("/api/listings/{listingId}")
        .then()
            .statusCode(404);
    }

    @Test
    public void findAll_returns200WithArray() {
        given()
        .when()
            .get("/api/listings")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void findByUserId_returnsOnlyThatUsersListings() {
        createOpenSurplusListing();

        given()
            .queryParam("userId", testUserId)
        .when()
            .get("/api/listings/myListings")
        .then()
            .statusCode(200)
            .body("userId", everyItem(equalTo(testUserId)));
    }

    // ---------- update ----------

    @Test
    public void update_ownOpenListing_returns200() {
        int listingId = createOpenSurplusListing();

        String body = """
            {
              "categoryId": %d,
              "type": "SURPLUS",
              "quantity": 8,
              "availableUntil": "%s"
            }
            """.formatted(SEEDED_CATEGORY_ID, LocalDate.now().plusDays(45));

        given()
            .pathParam("listingId", listingId)
            .queryParam("requesterId", testUserId)
            .contentType("application/json")
            .body(body)
        .when()
            .put("/api/listings/{listingId}")
        .then()
            .statusCode(200)
            .body("quantity", equalTo(8));
    }

    @Test
    public void update_asNonOwner_returns400() {
        int listingId = createOpenSurplusListing();
        int otherUserId = testUserId + 999999; // guaranteed not the owner

        String body = """
            {
              "categoryId": %d,
              "type": "SURPLUS",
              "quantity": 8,
              "availableUntil": "%s"
            }
            """.formatted(SEEDED_CATEGORY_ID, LocalDate.now().plusDays(45));

        given()
            .pathParam("listingId", listingId)
            .queryParam("requesterId", otherUserId)
            .contentType("application/json")
            .body(body)
        .when()
            .put("/api/listings/{listingId}")
        .then()
            .statusCode(400);
    }

    @Test
    public void update_nonexistentListing_returns404() {
        String body = """
            {
              "categoryId": %d,
              "type": "SURPLUS",
              "quantity": 8,
              "availableUntil": "%s"
            }
            """.formatted(SEEDED_CATEGORY_ID, LocalDate.now().plusDays(45));

        given()
            .pathParam("listingId", 9_999_999)
            .queryParam("requesterId", testUserId)
            .contentType("application/json")
            .body(body)
        .when()
            .put("/api/listings/{listingId}")
        .then()
            .statusCode(404);
    }

    // ---------- delete (withdraw) ----------

    @Test
    public void delete_ownOpenListing_returns204() {
        int listingId = createOpenSurplusListing();

        given()
            .pathParam("listingId", listingId)
            .queryParam("requesterId", testUserId)
        .when()
            .delete("/api/listings/{listingId}")
        .then()
            .statusCode(204);
    }

    @Test
    public void delete_asNonOwner_returns400() {
        int listingId = createOpenSurplusListing();
        int otherUserId = testUserId + 999999;

        given()
            .pathParam("listingId", listingId)
            .queryParam("requesterId", otherUserId)
        .when()
            .delete("/api/listings/{listingId}")
        .then()
            .statusCode(400);
    }

    @Test
    public void delete_alreadyWithdrawn_returns400() {
        int listingId = createOpenSurplusListing();

        // withdraw once
        given()
            .pathParam("listingId", listingId)
            .queryParam("requesterId", testUserId)
        .when()
            .delete("/api/listings/{listingId}")
        .then()
            .statusCode(204);

        // withdraw again - no longer OPEN
        given()
            .pathParam("listingId", listingId)
            .queryParam("requesterId", testUserId)
        .when()
            .delete("/api/listings/{listingId}")
        .then()
            .statusCode(400);
    }

    // ---------- match history ----------

    @Test
    public void getMatchHistory_existingListing_returns200() {
        int listingId = createOpenSurplusListing();

        given()
            .pathParam("listingId", listingId)
        .when()
            .get("/api/listings/{listingId}/matches")
        .then()
            .statusCode(200);
    }

    @Test
    public void getMatchHistory_nonexistentListing_returns404() {
        given()
            .pathParam("listingId", 9_999_999)
        .when()
            .get("/api/listings/{listingId}/matches")
        .then()
            .statusCode(404);
    }

    // ---------- helper ----------

    private int createOpenSurplusListing() {
        String body = """
            {
              "userId": %d,
              "categoryId": %d,
              "type": "SURPLUS",
              "quantity": 3,
              "availableUntil": "%s"
            }
            """.formatted(testUserId, SEEDED_CATEGORY_ID, LocalDate.now().plusDays(20));

        return given()
            .contentType("application/json")
            .body(body)
        .when()
            .post("/api/listings")
        .then()
            .statusCode(201)
            .extract().path("listingId");
    }
}
