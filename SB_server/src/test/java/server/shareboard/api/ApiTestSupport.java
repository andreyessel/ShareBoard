package server.shareboard.api;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeSuite;

/**
 * Shared setup for the REST Assured / TestNG API suite.
 *
 * These are black-box tests: they hit a REAL running instance of the
 * ShareBoard Spring Boot app over HTTP, the same way Postman or a
 * browser would. They do NOT use @SpringBootTest and do NOT start the
 * app themselves.
 *
 * Before running this suite:
 *   1. Start the app in one terminal:  mvn spring-boot:run
 *      (make sure DB_URL / DB_USERNAME / DB_PASSWORD are set, same as
 *      you already do for local dev)
 *   2. In a second terminal, run the suite (see TESTING_SETUP.md for
 *      the exact Maven command).
 */
public abstract class ApiTestSupport {

    protected static final String BASE_URL = System.getProperty("api.baseUrl", "http://localhost:8080");

    @BeforeSuite(alwaysRun = true)
    public void setUpRestAssured() {
        RestAssured.baseURI = BASE_URL;
    }

    /**
     * Small helper so every test class doesn't have to hand-build a
     * unique username. Timestamp-based suffix keeps parallel/local
     * test reruns from colliding on "username already taken".
     */
    protected static String uniqueUsername(String prefix) {
        return prefix + "_" + System.currentTimeMillis() % 1_000_000;
    }
}
