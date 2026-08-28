package server.shareboard.data;

import server.shareboard.models.MatchLine;

import java.util.List;

public class MatchLineJdbcClientRepository implements MatchLineRepository{
    @Override
    public List<MatchLine> findAll() {
        return List.of();
    }

    @Override
    public List<MatchLine> findByListingId(int listingId) {
        return List.of();
    }

    @Override
    public MatchLine create(MatchLine matchLine) {
        return null;
    }
}
