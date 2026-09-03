package server.shareboard.domain;

import org.springframework.stereotype.Component;
import server.shareboard.models.Listing;
import server.shareboard.models.ListingType;
import server.shareboard.models.MatchLine;
import server.shareboard.models.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
@Component
public class MatchingEngine {
    
    //Comparator for need
    Comparator<Listing> byNeedPriority = Comparator.comparing((Listing l) -> l.getUrgency()).reversed()
            .thenComparing(l -> l.getNeededBy())
            .thenComparing(l -> l.getCreatedAt());


    //Comparator for surplus
    Comparator<Listing> bySurplusPriority =Comparator.comparing((Listing l) -> l.getAvailableUntil())
            .thenComparing(Comparator.comparing((Listing l) -> l.getQuantityRemaining()));


    PriorityQueue<Listing> needQueue = new PriorityQueue<>(byNeedPriority);
    PriorityQueue<Listing> surplusQueue = new PriorityQueue<>(bySurplusPriority);



    //submitting a listing
    public MatchResult submit(Listing listing) {
        MatchResult result;

        if (listing.getType() == ListingType.NEED) {
            result = attemptMatch(listing);
            if (listing.getQuantityRemaining() > 0) {
                needQueue.add(listing);
            }
        } else if (listing.getType() == ListingType.SURPLUS) {
            result = attemptMatch(listing);
            if (listing.getQuantityRemaining() > 0) {
                surplusQueue.add(listing);
            }
        } else {
            result = new MatchResult(new ArrayList<>(), new ArrayList<>());
        }

        return result;
    }



    //matching a listing
    private MatchResult attemptMatch(Listing listing) {
        List<MatchLine> matches = new ArrayList<>();
        List<Listing> updatedListings = new ArrayList<>();

        PriorityQueue<Listing> opposite = (listing.getType() == ListingType.NEED)
                ? surplusQueue : needQueue;

        List<Listing> skipped = new ArrayList<>();

        while (listing.getQuantityRemaining() > 0 && !opposite.isEmpty()) {
            Listing candidate = opposite.poll();

            boolean sameCategory = listing.getCategoryId() == candidate.getCategoryId();
            boolean compatible = (listing.getType() == ListingType.NEED)
                    ? datesAreCompatible(listing.getNeededBy(), candidate.getAvailableUntil())
                    : datesAreCompatible(candidate.getNeededBy(), listing.getAvailableUntil());

            if (!sameCategory || !compatible) {
                skipped.add(candidate);
            } else {
                int matchedQuantity = Math.min(listing.getQuantityRemaining(), candidate.getQuantityRemaining());
                listing.setQuantityRemaining(listing.getQuantityRemaining() - matchedQuantity);
                candidate.setQuantityRemaining(candidate.getQuantityRemaining() - matchedQuantity);

                updateStatusAfterMatch(listing);
                updateStatusAfterMatch(candidate);

                int needListingId = (listing.getType() == ListingType.NEED) ? listing.getListingId() : candidate.getListingId();
                int surplusListingId = (listing.getType() == ListingType.SURPLUS) ? listing.getListingId() : candidate.getListingId();

                MatchLine match = new MatchLine(0, needListingId, surplusListingId, matchedQuantity, LocalDateTime.now());
                matches.add(match);

                updatedListings.add(candidate);

                if (candidate.getQuantityRemaining() > 0) {
                    skipped.add(candidate);
                }
            }
        }

        if (!matches.isEmpty()) {
            updatedListings.add(listing);
        }

        opposite.addAll(skipped);
        return new MatchResult(matches, updatedListings); 
    }


    //checking which listing has cross the deadline
    public boolean datesAreCompatible(LocalDate neededBy, LocalDate availableUntil){
        if (availableUntil.isAfter(neededBy) || availableUntil.isEqual(neededBy)) {
            return true;
        } else {
            return false;
        }
    }


    //checking status after a match
    private void updateStatusAfterMatch(Listing listing) {
        if (listing.getQuantityRemaining() == 0){
            listing.setStatus(Status.MATCHED);
        }else{
            listing.setStatus(Status.PARTIALLY_MATCHED);
        }
    }


    public void withdraw(int listingId, ListingType type) {
        if (type == ListingType.NEED) {
            needQueue.removeIf(l -> l.getListingId() == listingId);
        } else {
            surplusQueue.removeIf(l -> l.getListingId() == listingId);
        }
    }
}