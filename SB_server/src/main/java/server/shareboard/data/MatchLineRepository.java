package server.shareboard.data;

import server.shareboard.domain.MatchHistoryEntry;
import server.shareboard.domain.RecentMatchEntry;
import server.shareboard.models.MatchLine;

import java.util.List;

public interface MatchLineRepository {

    List<MatchLine> findAll();

    List<MatchLine> findByListingId(int listingId);
    // list of match history
    List<MatchHistoryEntry> findHistoryForListing(int listingId);

    List<RecentMatchEntry> findRecent(int limit);

    MatchLine create(MatchLine matchLine);
}