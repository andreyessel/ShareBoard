package server.shareboard.data;

import server.shareboard.models.MatchLine;

import java.util.List;

public interface MatchLineRepository {

    List<MatchLine> findAll();

    List<MatchLine> findByListingId(int listingId);

    MatchLine create(MatchLine matchLine);
}