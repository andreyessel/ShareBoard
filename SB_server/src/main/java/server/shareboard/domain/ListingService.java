package server.shareboard.domain;

import org.springframework.stereotype.Service;
import server.shareboard.data.ListingRepository;
import server.shareboard.data.MatchLineRepository;
import server.shareboard.models.Listing;
import server.shareboard.models.MatchLine;

import java.util.List;

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

   public Result<Listing> create(Listing listing) {
        Result<Listing> result = new Result<>();

        //Validating a listing to be created
        if (listing == null) {
            result.addErrorMessage("Listing cannot be null",ResultType.INVALID);
            return result;
        }
        if (listing.getUserId() <= 0) {
            result.addErrorMessage("userId is required", ResultType.INVALID);
        }
        if (listing.getCategoryId() <= 0) {
            result.addErrorMessage("categoryId is required", ResultType.INVALID);
        }
        if (listing.getType() == null) {
            result.addErrorMessage("type is required", ResultType.INVALID);
        }
        if (listing.getQuantityRemaining() <= 0 ) {
            result.addErrorMessage("quantity must be greater than 0", ResultType.INVALID);
        }
        if (!result.isSuccess()){
            return  result;
        }
        if (listing.getListingId() != 0) {
            result.addErrorMessage("listingId cannot be set for `create` operation", ResultType.INVALID);
            return result;
        }
        Listing created = repository.create(listing);
        if (created == null){
            result.addErrorMessage("user can not be null", ResultType.INVALID);
            return result;
        }

     List<MatchLine> matches = matchingEngine.submit(created);

     for (MatchLine match : matches){
         matchLineRepository.create(match);
     }
     //after mutations
        repository.update(created);

        result.setPayload(created);
        return result;
    }


    public Result<Listing> findById(int listingId) {
        Result<Listing> result = new Result<>();
        Listing found = repository.findById(listingId);
        if (found == null) {
            result.addErrorMessage("Listing not found", ResultType.NOT_FOUND);
        } else {
            result.setPayload(found);
        }
        return result;
    }
}