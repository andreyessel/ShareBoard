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
        Listing need = new Listing(1, 2, 2, ListingType.NEED, 20, 20, Urgency.HIGH, LocalDate.of(2026, 8, 30), null, Status.OPEN, LocalDateTime.now());
        Listing surplus = new Listing(2, 5, 2, ListingType.SURPLUS, 20, 20, null, null, LocalDate.of(2026, 9, 1), Status.OPEN, LocalDateTime.of(2026, 8, 24, 1, 1, 1, 1));
        MatchingEngine engine = new MatchingEngine();

        //Act
        engine.submit(need);
        MatchResult result = engine.submit(surplus);
        List<MatchLine> matches = result.getMatches();

        //Assert
        assertEquals(1, matches.size());
        assertEquals(0, need.getQuantityRemaining());
        assertEquals(0, surplus.getQuantityRemaining());
        assertEquals(Status.MATCHED, need.getStatus());
        assertEquals(Status.MATCHED, surplus.getStatus());
    }

    @Test
    void datesAreCompatibleWhenSurplusAvailableUntilIsAfterNeeded() {
        LocalDate needBy = LocalDate.of(2026, 8, 1);
        LocalDate surplusAvailableUntil = LocalDate.of(2026, 9, 1);
        MatchingEngine engine = new MatchingEngine();

        boolean result = engine.datesAreCompatible(needBy, surplusAvailableUntil);

        assertTrue(result);
    }

    @Test
    void datesAreNotCompatiblesWhenSurplusIsAvailableUntilIsBeforeNeeded() {
        LocalDate needBy = LocalDate.of(2026, 7, 1);
        LocalDate surplusAvailableUntil = LocalDate.of(2026, 6, 1);
        MatchingEngine engine = new MatchingEngine();

        boolean result = engine.datesAreCompatible(needBy, surplusAvailableUntil);

        assertFalse(result);
    }

}