package server.shareboard.domain;

import org.springframework.stereotype.Service;
import server.shareboard.data.ListingRepository;
import server.shareboard.data.MatchLineRepository;
import server.shareboard.models.Listing;
import server.shareboard.models.ListingType;
import server.shareboard.models.MatchLine;
import server.shareboard.models.Status;

import java.time.LocalDateTime;
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
        if (listing.getQuantity() <= 0 ) {
            result.addErrorMessage("quantity must be greater than 0", ResultType.INVALID);
        }
        if (!result.isSuccess()){
            return  result;
        }
        if (listing.getListingId() != 0) {
            result.addErrorMessage("listingId cannot be set for `create` operation", ResultType.INVALID);
            return result;
        }

       listing.setQuantityRemaining(listing.getQuantity());
       listing.setCreatedAt(LocalDateTime.now());
       Listing created = repository.create(listing);
        if (created == null){
            result.addErrorMessage("user can not be null", ResultType.INVALID);
            return result;
        }

     MatchResult matchResult = matchingEngine.submit(created);

     for (MatchLine match : matchResult.getMatches()){
         matchLineRepository.create(match);
     }
       for (Listing updated : matchResult.getUpdatedListings()) {
           repository.update(updated);
       }

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

    public Result<List<Listing>> findAll(){
        Result<List<Listing>> result = new Result<>();
        List<Listing> listings = repository.findAll();
        result.setPayload(listings);
        return result;
    }

    public Result<List<Listing>> findByUserId(int userId) {
        Result<List<Listing>> result = new Result<>();
      List<Listing> found = repository.findByUserId(userId);
        result.setPayload(found);
        return result;
    }

    // ListingService.java — add this method (needs `import server.shareboard.models.Status;`)

    public Result<Listing> update(Listing listing, int requesterId) {
        Result<Listing> result = new Result<>();

        if (listing == null) {
            result.addErrorMessage("Listing cannot be null", ResultType.INVALID);
            return result;
        }

        Listing existing = repository.findById(listing.getListingId());
        if (existing == null) {
            result.addErrorMessage("Listing not found", ResultType.NOT_FOUND);
            return result;
        }

        if (existing.getUserId() != requesterId) {
            result.addErrorMessage("You can only edit your own listings", ResultType.INVALID);
            return result;
        }

        if (existing.getStatus() != Status.OPEN) {
            result.addErrorMessage("Only OPEN listings can be edited", ResultType.INVALID);
            return result;
        }

        if (listing.getCategoryId() <= 0) {
            result.addErrorMessage("categoryId is required", ResultType.INVALID);
            return result;
        }
        if (listing.getQuantity() <= 0) {
            result.addErrorMessage("quantity must be greater than 0", ResultType.INVALID);
            return result;
        }

        if (existing.getType() == ListingType.SURPLUS) {
            if (existing.getUrgency() != null || existing.getNeededBy() != null){
                result.addErrorMessage("You cannot edit `SURPLUS` with a different type", ResultType.INVALID);
                return result;
            }
        }
        if (existing.getType() == ListingType.NEED) {
            if(existing.getAvailableUntil() != null) {
                result.addErrorMessage("You cannot edit `NEED` with a different tpye",ResultType.INVALID );
            }
        }
        existing.setCategoryId(listing.getCategoryId());
        existing.setQuantity(listing.getQuantity());
        existing.setQuantityRemaining(listing.getQuantity());
        existing.setUrgency(listing.getUrgency());
        existing.setNeededBy(listing.getNeededBy());
        existing.setAvailableUntil(listing.getAvailableUntil());

        boolean updated = repository.update(existing);
        if (!updated) {
            result.addErrorMessage("Could not update listing", ResultType.INVALID);
            return result;
        }

        result.setPayload(existing);
        return result;
    }


    public Result<Listing> deleteById(int listingId, int requesterId) {
        Result<Listing> result = new Result<>();

        Listing existing = repository.findById(listingId);
        if (existing == null) {
            result.addErrorMessage("Listing not found", ResultType.NOT_FOUND);
            return result;
        }

        if (existing.getUserId() != requesterId) {
            result.addErrorMessage("You can only delete your own listings", ResultType.INVALID);
            return result;
        }

        if (existing.getStatus() != Status.OPEN) {
            result.addErrorMessage("Only OPEN listings can be withdrawn", ResultType.INVALID);
            return result;
        }

        boolean deleted = repository.deleteById(listingId);
        if (!deleted) {
            result.addErrorMessage("Could not delete listing", ResultType.INVALID);
            return result;
        }

        result.setPayload(existing);
        return result;
    }


}