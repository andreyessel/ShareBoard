package server.shareboard.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchingEngineTest {

    @Test
    void shouldFullyMatchWhenQuantitiesAreEqual() {
    // continue tomorrow
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