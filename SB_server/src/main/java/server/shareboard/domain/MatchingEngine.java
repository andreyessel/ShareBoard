package server.shareboard.domain;

import server.shareboard.models.Listing;
import server.shareboard.models.MatchLine;

import java.time.LocalDate;
import java.util.List;

public class MatchingEngine {

    //submitting a listing
    public List<MatchLine> submit(Listing listing){

        return null;
    }

    //matching a listing
    public void attemptMatch( /*incoming, otherSideQueue*/) {

    }

    //checking which listing has cross the deadline
    public boolean deadlineCrosses(LocalDate neededBy, LocalDate availableUntil){

        return false;
    }
}
