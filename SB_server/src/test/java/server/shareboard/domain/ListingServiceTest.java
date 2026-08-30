package server.shareboard.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.simple.JdbcClient;
import server.shareboard.data.ListingRepository;
import server.shareboard.models.Listing;
import server.shareboard.models.ListingType;
import server.shareboard.models.Status;
import server.shareboard.models.Urgency;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ListingServiceTest {

    @Autowired
    ListingService service;

    @Autowired
    ListingRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldSaveBothListingsWhenAMatchOccurs() {
        //Arrange
        Listing need = new Listing(0, 1, 1, ListingType.NEED, 10, 10, Urgency.HIGH, LocalDate.of(2026, 9, 1), null, Status.OPEN, java.time.LocalDateTime.now());
        Listing surplus = new Listing(0, 2, 1, ListingType.SURPLUS, 10, 10, null, null, LocalDate.of(2026, 9, 10), Status.OPEN, java.time.LocalDateTime.now());

        //Act
        Result<Listing> needResult = service.create(need);
        Result<Listing> surplusResult = service.create(surplus);

        Listing SaveNeed = repository.findById(needResult.getPayload().getListingId());
        Listing SaveSurplus = repository.findById(surplusResult.getPayload().getListingId());

        //Assert
        assertEquals(0, SaveNeed.getQuantityRemaining());
        assertEquals(0, SaveSurplus.getQuantityRemaining());
        assertEquals(Status.MATCHED, SaveNeed.getStatus());
        assertEquals(Status.MATCHED, SaveSurplus.getStatus());
    }

    @Test
    void shouldFindByUserId() {
        //Arrange
        Listing listing = new Listing(0, 2, 1, ListingType.NEED, 10, 10, Urgency.HIGH, LocalDate.of(2026, 9, 1), null, Status.OPEN, java.time.LocalDateTime.now());
        service.create(listing);

        //Act
        Result<List<Listing>> result = service.findByUserId(1);

        //Assert
        assertTrue(result.isSuccess());
        assertEquals(1, result.getPayload().size());
        assertEquals(1, result.getPayload().get(0).getUserId());
    }

    @Test
    void shouldUpdateAnExistingListing() {
        //Arrange
        Listing listing = new Listing(0, 2, 1, ListingType.NEED, 10, 10, Urgency.HIGH, LocalDate.of(2026, 9, 1), null, Status.OPEN, java.time.LocalDateTime.now());
        Result<Listing> createResult = service.create(listing);
        int listingId = createResult.getPayload().getListingId();

        Listing edited = new Listing(listingId, 2, 1, ListingType.NEED, 18, 18, Urgency.MEDIUM, LocalDate.of(2026, 9, 15), null, Status.OPEN, java.time.LocalDateTime.now());

        //Act
        Result<Listing> result = service.update(edited, 2);

        //Assert
        assertTrue(result.isSuccess());
        assertEquals(18, result.getPayload().getQuantity());
        assertEquals(Urgency.MEDIUM, result.getPayload().getUrgency());

        Listing persisted = repository.findById(listingId);
        assertEquals(18, persisted.getQuantity());
    }

    @Test
    void shouldDeleteByIdWhenRequesterOwnsIt() {
        //Arrange
        Listing listing = new Listing(0, 2, 1, ListingType.NEED, 10, 10, Urgency.HIGH,
                LocalDate.of(2026, 9, 1), null, Status.OPEN, java.time.LocalDateTime.now());
        Result<Listing> createResult = service.create(listing);
        int listingId = createResult.getPayload().getListingId();

        //Act
        Result<Listing> result = service.deleteById(listingId, 2);

        //Assert
        assertTrue(result.isSuccess());
        assertNull(repository.findById(listingId));
    }

    @Test
    void shouldNotDeleteWhenRequesterIsNotTheOwner() {
        //Arrange
        Listing listing = new Listing(0, 2, 1, ListingType.NEED, 10, 10, Urgency.HIGH,
                LocalDate.of(2026, 9, 1), null, Status.OPEN, java.time.LocalDateTime.now());
        Result<Listing> createResult = service.create(listing);
        int listingId = createResult.getPayload().getListingId();

        //Act
        Result<Listing> result = service.deleteById(listingId, 999);

        //Assert
        assertFalse(result.isSuccess());
        assertEquals(ResultType.INVALID, result.getResultType());
        assertNotNull(repository.findById(listingId));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingListingThatDoesNotExist() {
        //Act
        Result<Listing> result = service.deleteById(9999, 2);

        //Assert
        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
    }
}

