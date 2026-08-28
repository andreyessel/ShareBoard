package server.shareboard.domain;

import server.shareboard.models.Listing;
import server.shareboard.models.MatchLine;

import java.util.List;

public class MatchResult {
    private final List<MatchLine> matches;
    private final List<Listing> updatedListings;

    public MatchResult(List<MatchLine> matches, List<Listing> updatedListings) {
        this.matches = matches;
        this.updatedListings = updatedListings;
    }

    public List<MatchLine> getMatches() {
        return matches;
    }

    public List<Listing> getUpdatedListings() {
        return updatedListings;
    }
}