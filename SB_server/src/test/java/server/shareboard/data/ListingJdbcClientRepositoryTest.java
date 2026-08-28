package server.shareboard.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import server.shareboard.models.Listing;
import server.shareboard.models.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ListingJdbcClientRepositoryTest {

    @Autowired
    private ListingJdbcClientRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    // find all
    @Test
    void shouldFindAllListing() {
        //Act
       List<Listing> allListings = repository.findAll();

       //Assert
        assertNotNull(allListings);
    }

    //happy path
    @Test
    void findByIdHappyPath() {
        //Act
        Listing found = repository.findById(1);

        //Assert
        assertEquals(1,found.getListingId());
        assertNotNull(found);
    }

    //unhappy path
    @Test
    void findByIdListingNotFound() {
        //Act
        Listing notFound = repository.findById(888);

        //Assert
        assertNull(notFound);
    }

    //happy path
    @Test
    void findByUserIdHappyPath() {
        //Act
      List<Listing> found = repository.findByUserId(1);

      //Assert
        assertNotNull(found);
        assertEquals(1, found.size());
    }

    //unhappy path
    @Test
    void findByUserIdUHappyPath_UserNotFound() {
        //Act
        List<Listing> notFound = repository.findByUserId(888);

        //Assert
        assertEquals(0, notFound.size());
    }

    //happy path
    @Test
    void shouldCreateANewUser() {
        //Arrange
        Listing listing = TestDataHelper.listingToCreate();

        //Act
        Listing actual = repository.createListing(listing);

        //Assert
        assertNotNull(actual);
        assertEquals(3,actual.getListingId());

    }

}