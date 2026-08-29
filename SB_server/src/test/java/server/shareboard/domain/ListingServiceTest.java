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
}

