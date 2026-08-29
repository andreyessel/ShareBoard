package server.shareboard.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import server.shareboard.models.MatchLine;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MatchLineJdbcClientRepositoryTest {

    @Autowired
    private MatchLineJdbcClientRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    @Test
    void shouldCreateAMatchLine() {
        //Arrange
        MatchLine matchLine = new MatchLine(0, 1, 2, 5, LocalDateTime.now());

        //Act
        MatchLine actual = repository.create(matchLine);

        //Assert
        assertNotNull(actual);
        assertTrue(actual.getMatchLineId() > 0);
        assertEquals(1, actual.getNeedListingId());
        assertEquals(2, actual.getSurplusListingId());
        assertEquals(5, actual.getQuantityMatched());
    }

    @Test
    void shouldFindByListingIdWhenListingIsTheNeedSide() {
        //Arrange
        MatchLine matchLine = new MatchLine(0, 1, 2, 5, LocalDateTime.now());
        repository.create(matchLine);

        //Act
        List<MatchLine> found = repository.findByListingId(1);

        //Assert
        assertEquals(1, found.size());
        assertEquals(1, found.get(0).getNeedListingId());
    }

    @Test
    void shouldFindByListingIdWhenListingIsTheSurplusSide() {
        //Arrange
        MatchLine matchLine = new MatchLine(0, 1, 2, 5, LocalDateTime.now());
        repository.create(matchLine);

        //Act
        List<MatchLine> found = repository.findByListingId(2);

        //Assert
        assertEquals(1, found.size());
        assertEquals(2, found.get(0).getSurplusListingId());
    }

    @Test
    void shouldReturnEmptyListWhenListingHasNoMatches() {
        //Act
        List<MatchLine> found = repository.findByListingId(999);

        //Assert
        assertTrue(found.isEmpty());
    }
}