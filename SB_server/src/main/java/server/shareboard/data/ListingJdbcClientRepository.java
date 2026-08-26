package server.shareboard.data;

import server.shareboard.models.Listing;

import java.util.List;
import java.util.Optional;

public class ListingJdbcClientRepository implements ListingRepository{
    @Override
    public List<Listing> findAll() {
        return List.of();
    }

    @Override
    public Optional<Listing> findById(int listingId) {
        return Optional.empty();
    }

    @Override
    public List<Listing> findByUserId(int userId) {
        return List.of();
    }

    @Override
    public Listing create(Listing listing) {
        return null;
    }

    @Override
    public boolean update(Listing listing) {
        return false;
    }

    @Override
    public boolean deleteById(int listingId) {
        return false;
    }
}
