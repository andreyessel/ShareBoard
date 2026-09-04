package server.shareboard.data;

import server.shareboard.models.Listing;

import java.util.List;
import java.util.Optional;

public interface ListingRepository {

    List<Listing> findAll();

    Listing findById(int listingId);

    List<Listing> findByUserId(int userId);

    Listing create(Listing listing);

    boolean update(Listing listing);


}