package server.shareboard.domain;

import org.junit.jupiter.api.Test;
import server.shareboard.models.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchingEngineTest {

    @Test
    void shouldFullyMatchWhenQuantitiesAreEqual() {
        //Arrange
        Listing need = new Listing(1,2,2, ListingType.NEED,20,20, Urgency.HIGH, LocalDate.of(2026,8,30), null,Status.OPEN, LocalDateTime.now());
        Listing surplus = new Listing(2,5,2,ListingType.SURPLUS,20,20,null,null,LocalDate.of(2026,9,1),Status.OPEN,LocalDateTime.of(2026,8,24,1,1,1,1));
        MatchingEngine engine = new MatchingEngine();

        //Act
        engine.submit(need);
        List<MatchLine> matches = engine.submit(surplus);

        //Assert
        assertEquals(1,matches.size());
        assertEquals(0, need.getQuantityRemaining());
        assertEquals(0, surplus.getQuantityRemaining());
    }

    @Test
    void datesAreCompatibleWhenSurplusAvailableUntilIsAfterNeeded() {
        //Arrange
        LocalDate needBy = LocalDate.of(2026,8,1);
        LocalDate surplusAvailableUntil = LocalDate.of(2026,9, 1);
        MatchingEngine engine = new MatchingEngine();

        //Act
        boolean result = engine.datesAreCompatible(needBy,surplusAvailableUntil);

        //Assert
        assertTrue(result);
    }

    @Test
    void datesAreNotCompatiblesWhenSurplusIsAvailableUntilIsBeforeNeeded() {
        //Arrange
        LocalDate needBy = LocalDate.of(2026,7,1);
        LocalDate surplusAvailableUntil = LocalDate.of(2026,6, 1);
        MatchingEngine engine = new MatchingEngine();

        //Act
        boolean result = engine.datesAreCompatible(needBy,surplusAvailableUntil);

        //Asser
        assertFalse(result);
    }


    @Test
    void shouldNotMatchWhenCategoriesAreDifferent() {
        //Arrange
        Listing need = new Listing(1, 2, 1, ListingType.NEED, 20, 20, Urgency.HIGH,
                LocalDate.of(2026,8,30), null, Status.OPEN, LocalDateTime.now());
        Listing surplus = new Listing(2, 5, 2, ListingType.SURPLUS, 20, 20, null, null,
                LocalDate.of(2026,9,1), Status.OPEN, LocalDateTime.of(2026,8,24,1,1,1));
        MatchingEngine engine = new MatchingEngine();

        //Act
        engine.submit(need);
        List<MatchLine> matches = engine.submit(surplus);

        //Assert
        assertEquals(0, matches.size());
        assertEquals(20, need.getQuantityRemaining());
        assertEquals(20, surplus.getQuantityRemaining());
    }
}