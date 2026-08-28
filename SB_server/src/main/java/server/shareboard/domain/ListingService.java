package server.shareboard.domain;

import org.springframework.stereotype.Service;
import server.shareboard.data.ListingRepository;
import server.shareboard.data.MatchLineRepository;
import server.shareboard.models.Listing;

@Service
public class ListingService {

    private final ListingRepository repository;
    private final MatchingEngine matchingEngine;
    private final MatchLineRepository matchLineRepository;

    public ListingService(ListingRepository repository, MatchingEngine matchingEngine, MatchLineRepository matchLineRepository) {
        this.repository = repository;
        this.matchingEngine = matchingEngine;
        this.matchLineRepository = matchLineRepository;
    }

    Result<Listing> createListing(Listing listing) {
        Result<Listing> result =
    }
}
