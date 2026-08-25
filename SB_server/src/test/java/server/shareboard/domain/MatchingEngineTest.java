package server.shareboard.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchingEngineTest {

    @Test
    void shouldFullyMatchWhenQuantitiesAreEqual() {
        // Arrange: build the two listings you're testing with real field values —
        // a NEED and a SURPLUS, same category, deadlines that DO cross, equal quantity

        // Act: call engine.submit(...) — first for the resting listing, then the incoming one

        // Assert: what do you expect to be true afterward?
        //   - did a match get created?
        //   - is quantityRemaining now 0 on both sides?
        //   - is status now something like MATCHED?
    }

    @Test
    void deadlineCrossesWhenSurplusAvailableUntilIsBeforeNeeded() {
        //Arrange
        LocalDate needBy = LocalDate.of(2026,8,1);
        LocalDate surplusAvailableUntil = LocalDate.of(2026,7, 1);
        MatchingEngine engine = new MatchingEngine();

        //Act
        boolean result = engine.deadlineCrosses(needBy,surplusAvailableUntil);

        //Assert
        assertTrue(result);
    }

    @Test
    void deadLineDoesNotCrossWhenSurplusIsAvailableUntilIsAfterNeeded() {
        //Arrange
        LocalDate needBy = LocalDate.of(2026,7,1);
        LocalDate surplusAvailableUntil = LocalDate.of(2026,8, 1);
        MatchingEngine engine = new MatchingEngine();

        //Act
        boolean result = engine.deadlineCrosses(needBy,surplusAvailableUntil);

        //Asser
        assertFalse(result);
    }
}