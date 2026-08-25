package server.shareboard.domain;

import server.shareboard.models.Listing;
import server.shareboard.models.MatchLine;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MatchingEngine {

    //submitting a listing
    public List<MatchLine> submit(Listing listing){

        return new ArrayList<MatchLine>();
    }

    //matching a listing
    public List<MatchLine> attemptMatch(Listing listing, List<Listing> otherSide) {

        return List.of();
    }

    //checking which listing has cross the deadline
    public boolean deadlineCrosses(LocalDate neededBy, LocalDate availableUntil){
        if (availableUntil.isBefore(neededBy)) {
            return true;
        } else {
            return false;
        }
    }
}