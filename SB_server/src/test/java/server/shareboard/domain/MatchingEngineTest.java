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
        assertEquals(Status.MATCHED,need.getStatus());
        assertEquals(Status.MATCHED, surplus.getStatus());
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

    @Test
    void shouldPartiallyMatchWhenSurplusIsLesserThanNeed() {
        //Arrange
        Listing need = new Listing(1,2,2, ListingType.NEED,20,20, Urgency.HIGH, LocalDate.of(2026,8,30), null,Status.OPEN, LocalDateTime.now());
        Listing surplus = new Listing(2,5,2,ListingType.SURPLUS,8,8,null,null,LocalDate.of(2026,9,1),Status.OPEN,LocalDateTime.of(2026,8,24,1,1,1,1));
        MatchingEngine engine = new MatchingEngine();

        //Act
        engine.submit(need);
        List<MatchLine> matches =  engine.submit(surplus);

        //Assert
        assertEquals(1, matches.size());
        assertEquals(12, need.getQuantityRemaining());
        assertEquals(0, surplus.getQuantityRemaining());
        assertEquals(Status.PARTIALLY_MATCHED,need.getStatus());
        assertEquals(Status.MATCHED, surplus.getStatus());
    }

    @Test
    void shouldPartiallyMatchWhenSurplusIsMuchMoreThanNeed() {
        //Arrange
        Listing need = new Listing(1,2,2, ListingType.NEED,8,8, Urgency.HIGH, LocalDate.of(2026,8,30), null,Status.OPEN, LocalDateTime.now());
        Listing surplus = new Listing(2,5,2,ListingType.SURPLUS,20,20,null,null,LocalDate.of(2026,9,1),Status.OPEN,LocalDateTime.of(2026,8,24,1,1,1,1));
        MatchingEngine engine = new MatchingEngine();

        //Act
        engine.submit(need);
        List<MatchLine> matches =  engine.submit(surplus);

        //Assert
        assertEquals(1, matches.size());
        assertEquals(0, need.getQuantityRemaining());
        assertEquals(12, surplus.getQuantityRemaining());
        assertEquals(Status.MATCHED,need.getStatus());
        assertEquals(Status.PARTIALLY_MATCHED, surplus.getStatus());
    }

    @Test
    void multiplySurplusFillsOneNeed() {
        //Arrange
        Listing need = new Listing(1,2,2, ListingType.NEED,20,20, Urgency.HIGH, LocalDate.of(2026,8,30), null,Status.OPEN, LocalDateTime.now());
        Listing surplus1 = new Listing(2,5,2,ListingType.SURPLUS,8,8,null,null,LocalDate.of(2026,9,1),Status.OPEN,LocalDateTime.of(2026,8,24,1,1,1,1));
        Listing surplus2 = new Listing(3,5,2,ListingType.SURPLUS,15,15,null,null,LocalDate.of(2026,9,1),Status.OPEN,LocalDateTime.of(2026,8,24,1,1,1,1));
        MatchingEngine engine = new MatchingEngine();

        //Act
        engine.submit(need);
        List<MatchLine> matchesForSurplus1 =  engine.submit(surplus1);
        List<MatchLine> matchesForSurplus2 =  engine.submit(surplus2);


        //Assert
        assertEquals(1, matchesForSurplus1.size());
        assertEquals(1,matchesForSurplus2.size());
        assertEquals(0, need.getQuantityRemaining());
        assertEquals(0, surplus1.getQuantityRemaining());
        assertEquals(3, surplus2.getQuantityRemaining());
    }

    @Test
    void multiplyNeedWithSameUrgencyButDifferentDatesFilledByOneSurplus() {
        //Arrange
        Listing need1 = new Listing(1,2,2, ListingType.NEED,10,10, Urgency.HIGH, LocalDate.of(2026,8,30), null,Status.OPEN, LocalDateTime.now());
        Listing need2 = new Listing(2,5,2,ListingType.NEED,9,9,Urgency.HIGH,LocalDate.of(2026,9,2),null,Status.OPEN,LocalDateTime.of(2026,8,26,1,0,1,0));
        Listing surplus = new Listing(3,4,2,ListingType.SURPLUS,12,12,null,null,LocalDate.of(2026,9,4),Status.OPEN,LocalDateTime.of(2026,8,24,1,1,1,1));
        MatchingEngine engine = new MatchingEngine();

        //Act
        engine.submit(need1);
        engine.submit(need2);
        List<MatchLine> matches =  engine.submit(surplus);

        //Assert
        assertEquals(2, matches.size());
        assertEquals(0, need1.getQuantityRemaining());
        assertEquals(7, need2.getQuantityRemaining());
        assertEquals(0, surplus.getQuantityRemaining());
        assertEquals(Status.MATCHED, need1.getStatus());
        assertEquals(Status.MATCHED, surplus.getStatus());
        assertEquals(Status.PARTIALLY_MATCHED, need2.getStatus());
    }
}