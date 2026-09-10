package server.shareboard.domain;

import jakarta.annotation.PostConstruct;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import server.shareboard.data.ListingRepository;
import server.shareboard.data.MatchLineRepository;
import server.shareboard.models.Listing;
import server.shareboard.models.ListingType;
import server.shareboard.models.MatchLine;
import server.shareboard.models.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListingService {

    private final ListingRepository repository;
    private final MatchingEngine matchingEngine;
    private final MatchLineRepository matchLineRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ListingService(ListingRepository repository, MatchingEngine matchingEngine, MatchLineRepository matchLineRepository, SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.matchingEngine = matchingEngine;
        this.matchLineRepository = matchLineRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @PostConstruct
    public void warmStartMatchingEngine() {
        List<Listing> active = repository.findAll().stream()
                .filter(l -> l.getStatus() == Status.OPEN || l.getStatus() == Status.PARTIALLY_MATCHED)
                .filter(l -> l.getQuantityRemaining() > 0)
                .toList();

        for (Listing listing : active) {
            matchingEngine.loadExisting(listing);
        }
    }

    public Result<Listing> create(Listing listing) {
        Result<Listing> result = new Result<>();

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

        if (listing.getType() == ListingType.NEED) {
            if (listing.getNeededBy() == null) {
                result.addErrorMessage("Needed By date is required.", ResultType.INVALID);
            } else if (listing.getNeededBy().isBefore(LocalDate.now())) {
                result.addErrorMessage("Needed By date cannot be in the past.", ResultType.INVALID);
            }
            listing.setAvailableUntil(null);
        } else if (listing.getType() == ListingType.SURPLUS) {
            if (listing.getAvailableUntil() == null) {
                result.addErrorMessage("Available Until date is required.", ResultType.INVALID);
            } else if (listing.getAvailableUntil().isBefore(LocalDate.now())) {
                result.addErrorMessage("Available Until date cannot be in the past.", ResultType.INVALID);
            }
            listing.setNeededBy(null);
        }

        listing.setQuantityRemaining(listing.getQuantity());
        listing.setCreatedAt(LocalDateTime.now());
        listing.setStatus(Status.OPEN);
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

        List<Listing> toBroadcast = matchResult.getUpdatedListings().isEmpty()
                ? List.of(created)
                : matchResult.getUpdatedListings();
        messagingTemplate.convertAndSend("/topic/listings/" + listing.getCategoryId(), toBroadcast);

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

    public Result<List<MatchHistoryEntry>> getMatchHistory(int listingId) {
        Result<List<MatchHistoryEntry>> result = new Result<>();

        if (repository.findById(listingId) == null) {
            result.addErrorMessage("Listing not found", ResultType.NOT_FOUND);
            return result;
        }

        result.setPayload(matchLineRepository.findHistoryForListing(listingId));
        return result;
    }

    public List<RecentMatchEntry> getRecentMatches(int limit) {
        return matchLineRepository.findRecent(limit);
    }

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

        if (listing.getType() != existing.getType()) {
            result.addErrorMessage("Cannot change a listing's type", ResultType.INVALID);
            return result;
        }

        if (existing.getType() == ListingType.SURPLUS) {
            if (listing.getUrgency() != null || listing.getNeededBy() != null) {
                result.addErrorMessage("SURPLUS listings cannot have urgency or neededBy", ResultType.INVALID);
                return result;
            }
        }
        if (existing.getType() == ListingType.NEED) {
            if (listing.getAvailableUntil() != null) {
                result.addErrorMessage("NEED listings cannot have availableUntil", ResultType.INVALID);
                return result;
            }
        }

        existing.setCategoryId(listing.getCategoryId());
        existing.setQuantity(listing.getQuantity());
        existing.setQuantityRemaining(listing.getQuantity());

        if (existing.getType() == ListingType.NEED) {
            existing.setUrgency(listing.getUrgency());
            existing.setNeededBy(listing.getNeededBy());
        } else {
            existing.setAvailableUntil(listing.getAvailableUntil());
        }

        boolean updated = repository.update(existing);
        if (!updated) {
            result.addErrorMessage("Could not update listing", ResultType.INVALID);
            return result;
        }

        messagingTemplate.convertAndSend("/topic/listings/" + existing.getCategoryId(), List.of(existing));

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

        existing.setStatus(Status.WITHDRAWN);
        boolean updated = repository.update(existing);
        if (!updated) {
            result.addErrorMessage("Could not withdraw listing", ResultType.INVALID);
            return result;
        }

        matchingEngine.withdraw(listingId, existing.getType());

        messagingTemplate.convertAndSend("/topic/listings/" + existing.getCategoryId(), List.of(existing));

        result.setPayload(existing);
        return result;
    }
}