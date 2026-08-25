package server.shareboard.domain;

import static org.junit.jupiter.api.Assertions.*;

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
}