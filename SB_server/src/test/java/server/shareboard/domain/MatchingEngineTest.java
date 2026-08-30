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

        //Assert
        assertEquals(1, result.getMatches().size());
        assertEquals(0, need.getQuantityRemaining());
        assertEquals(0, surplus.getQuantityRemaining());
        assertEquals(Status.MATCHED, need.getStatus());
        assertEquals(Status.MATCHED, surplus.getStatus());
    }

    @Test
    void datesAreCompatibleWhenSurplusAvailableUntilIsAfterNeeded() {
        //Arrange
        LocalDate needBy = LocalDate.of(2026, 8, 1);
        LocalDate surplusAvailableUntil = LocalDate.of(2026, 9, 1);
        MatchingEngine engine = new MatchingEngine();

        //Act
        boolean result = engine.datesAreCompatible(needBy, surplusAvailableUntil);

        //Assert
        assertTrue(result);
    }

    @Test
    void datesAreNotCompatiblesWhenSurplusIsAvailableUntilIsBeforeNeeded() {
        //Arrange
        LocalDate needBy = LocalDate.of(2026, 7, 1);
        LocalDate surplusAvailableUntil = LocalDate.of(2026, 6, 1);
        MatchingEngine engine = new MatchingEngine();

        //Act
        boolean result = engine.datesAreCompatible(needBy, surplusAvailableUntil);

        //Assert
        assertFalse(result);
    }

    @Test
    void shouldNotMatchWhenCategoriesAreDifferent() {
        //Arrange
        Listing need = new Listing(1, 2, 1, ListingType.NEED, 20, 20, Urgency.HIGH,
                LocalDate.of(2026, 8, 30), null, Status.OPEN, LocalDateTime.now());
        Listing surplus = new Listing(2, 5, 2, ListingType.SURPLUS, 20, 20, null, null,
                LocalDate.of(2026, 9, 1), Status.OPEN, LocalDateTime.of(2026, 8, 24, 1, 1, 1, 1));
        MatchingEngine engine = new MatchingEngine();

        //Act
        engine.submit(need);
        MatchResult result = engine.submit(surplus);

        //Assert
        assertEquals(0, result.getMatches().size());
        assertEquals(20, need.getQuantityRemaining());
        assertEquals(20, surplus.getQuantityRemaining());
    }

    @Test
    void shouldPartiallyMatchWhenSurplusIsLesserThanNeed() {
        //Arrange
        Listing need = new Listing(1, 2, 2, ListingType.NEED, 20, 20, Urgency.HIGH, LocalDate.of(2026, 8, 30), null, Status.OPEN, LocalDateTime.now());
        Listing surplus = new Listing(2, 5, 2, ListingType.SURPLUS, 8, 8, null, null, LocalDate.of(2026, 9, 1), Status.OPEN, LocalDateTime.of(2026, 8, 24, 1, 1, 1, 1));
        MatchingEngine engine = new MatchingEngine();

        //Act
        engine.submit(need);
        MatchResult result = engine.submit(surplus);

        //Assert
        assertEquals(1, result.getMatches().size());
        assertEquals(12, need.getQuantityRemaining());
        assertEquals(0, surplus.getQuantityRemaining());
        assertEquals(Status.PARTIALLY_MATCHED, need.getStatus());
        assertEquals(Status.MATCHED, surplus.getStatus());
    }

    @Test
    void shouldPartiallyMatchWhenSurplusIsMuchMoreThanNeed() {
        //Arrange
        Listing need = new Listing(1, 2, 2, ListingType.NEED, 8, 8, Urgency.HIGH, LocalDate.of(2026, 8, 30), null, Status.OPEN, LocalDateTime.now());
        Listing surplus = new Listing(2, 5, 2, ListingType.SURPLUS, 20, 20, null, null, LocalDate.of(2026, 9, 1), Status.OPEN, LocalDateTime.of(2026, 8, 24, 1, 1, 1, 1));
        MatchingEngine engine = new MatchingEngine();

        //Act
        engine.submit(need);
        MatchResult result = engine.submit(surplus);

        //Assert
        assertEquals(1, result.getMatches().size());
        assertEquals(0, need.getQuantityRemaining());
        assertEquals(12, surplus.getQuantityRemaining());
        assertEquals(Status.MATCHED, need.getStatus());
        assertEquals(Status.PARTIALLY_MATCHED, surplus.getStatus());
    }

    @Test
    void multiplySurplusFillsOneNeed() {
        //Arrange
        Listing need = new Listing(1, 2, 2, ListingType.NEED, 20, 20, Urgency.HIGH, LocalDate.of(2026, 8, 30), null, Status.OPEN, LocalDateTime.now());
        Listing surplus1 = new Listing(2, 5, 2, ListingType.SURPLUS, 8, 8, null, null, LocalDate.of(2026, 9, 1), Status.OPEN, LocalDateTime.of(2026, 8, 24, 1, 1, 1, 1));
        Listing surplus2 = new Listing(3, 5, 2, ListingType.SURPLUS, 15, 15, null, null, LocalDate.of(2026, 9, 1), Status.OPEN, LocalDateTime.of(2026, 8, 24, 1, 1, 1, 1));
        MatchingEngine engine = new MatchingEngine();

        //Act
        engine.submit(need);
        MatchResult resultForSurplus1 = engine.submit(surplus1);
        MatchResult resultForSurplus2 = engine.submit(surplus2);

        //Assert
        assertEquals(1, resultForSurplus1.getMatches().size());
        assertEquals(1, resultForSurplus2.getMatches().size());
        assertEquals(0, need.getQuantityRemaining());
        assertEquals(0, surplus1.getQuantityRemaining());
        assertEquals(3, surplus2.getQuantityRemaining());
    }

    @Test
    void multiplyNeedWithSameUrgencyButDifferentDatesFilledByOneSurplus() {
        //Arrange
        Listing need1 = new Listing(1, 2, 2, ListingType.NEED, 10, 10, Urgency.HIGH, LocalDate.of(2026, 8, 30), null, Status.OPEN, LocalDateTime.now());
        Listing need2 = new Listing(2, 5, 2, ListingType.NEED, 9, 9, Urgency.HIGH, LocalDate.of(2026, 9, 2), null, Status.OPEN, LocalDateTime.of(2026, 8, 26, 1, 0, 1, 0));
        Listing surplus = new Listing(3, 4, 2, ListingType.SURPLUS, 12, 12, null, null, LocalDate.of(2026, 9, 4), Status.OPEN, LocalDateTime.of(2026, 8, 24, 1, 1, 1, 1));
        MatchingEngine engine = new MatchingEngine();

        //Act
        engine.submit(need1);
        engine.submit(need2);
        MatchResult result = engine.submit(surplus);

        //Assert
        assertEquals(2, result.getMatches().size());
        assertEquals(0, need1.getQuantityRemaining());
        assertEquals(7, need2.getQuantityRemaining());
        assertEquals(0, surplus.getQuantityRemaining());
        assertEquals(Status.MATCHED, need1.getStatus());
        assertEquals(Status.MATCHED, surplus.getStatus());
        assertEquals(Status.PARTIALLY_MATCHED, need2.getStatus());
    }

    // NEW — this is the one that actually proves the .reversed() decision,
    // using your exact quantities: 2, 3, 6, 66
    @Test
    void surplusTieBreakPicksSmallestQuantityRemainingFirst() {
        //Arrange — four surpluses, same category, same availableUntil (a genuine tie),
        // submitted while no NEED exists yet, so they all rest in surplusQueue together
        Listing surplusA = new Listing(1, 5, 1, ListingType.SURPLUS, 66, 66, null, null, LocalDate.of(2026, 12, 1), Status.OPEN, LocalDateTime.now());
        Listing surplusB = new Listing(2, 5, 1, ListingType.SURPLUS, 2, 2, null, null, LocalDate.of(2026, 12, 1), Status.OPEN, LocalDateTime.now());
        Listing surplusC = new Listing(3, 5, 1, ListingType.SURPLUS, 6, 6, null, null, LocalDate.of(2026, 12, 1), Status.OPEN, LocalDateTime.now());
        Listing surplusD = new Listing(4, 5, 1, ListingType.SURPLUS, 3, 3, null, null, LocalDate.of(2026, 12, 1), Status.OPEN, LocalDateTime.now());
        MatchingEngine engine = new MatchingEngine();

        engine.submit(surplusA); // 66
        engine.submit(surplusB); // 2
        engine.submit(surplusC); // 6
        engine.submit(surplusD); // 3

        // One big NEED that can consume all four, revealing poll order via match sequence
        Listing need = new Listing(5, 9, 1, ListingType.NEED, 100, 100, Urgency.HIGH, LocalDate.of(2026, 11, 1), null, Status.OPEN, LocalDateTime.now());

        //Act
        MatchResult result = engine.submit(need);
        List<MatchLine> matches = result.getMatches();

        //Assert — the ORDER of quantityMatched values reveals which surplus got polled first.
        // Without .reversed(): ascending by quantityRemaining -> smallest (2) first, then 3, then 6, then 66.
        assertEquals(4, matches.size());
        assertEquals(2, matches.get(0).getQuantityMatched());
        assertEquals(3, matches.get(1).getQuantityMatched());
        assertEquals(6, matches.get(2).getQuantityMatched());
        assertEquals(66, matches.get(3).getQuantityMatched());
        assertEquals(23, need.getQuantityRemaining()); // 100 - (2+3+6+66) = 23
        assertEquals(Status.PARTIALLY_MATCHED, need.getStatus());
    }

    @Test
    void shouldNotMatchWhenQuantitiesAndQuantityRemainingAreNotEqual() {
        //Arrange
        Listing need = new Listing(1, 2, 2, ListingType.NEED, 3, 20, Urgency.HIGH, LocalDate.of(2026, 8, 30), null, Status.OPEN, LocalDateTime.now());
        Listing surplus = new Listing(2, 5, 2, ListingType.SURPLUS, 20, 20, null, null, LocalDate.of(2026, 9, 1), Status.OPEN, LocalDateTime.of(2026, 8, 24, 1, 1, 1, 1));
        MatchingEngine engine = new MatchingEngine();

        //Act
        engine.submit(need);
        MatchResult result = engine.submit(surplus);

        //Assert
        assertEquals(1, result.getMatches().size());
        assertEquals(0, need.getQuantityRemaining());
        assertEquals(0, surplus.getQuantityRemaining());
        assertEquals(Status.MATCHED, need.getStatus());
        assertEquals(Status.MATCHED, surplus.getStatus());
    }
}