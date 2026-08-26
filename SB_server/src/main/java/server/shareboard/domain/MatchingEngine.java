package server.shareboard.domain;

import server.shareboard.models.Listing;
import server.shareboard.models.ListingType;
import server.shareboard.models.MatchLine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class MatchingEngine {
    //Comparator for need
    Comparator<Listing> byNeedPriority = Comparator.comparing((Listing l) -> l.getUrgency()).reversed()
            .thenComparing(l -> l.getNeededBy())
            .thenComparing(l -> l.getCreatedAt());

    //Comparator for surplus
    Comparator<Listing> bySurplusPriority =Comparator.comparing((Listing l) -> l.getAvailableUntil())
            .thenComparing(Comparator.comparing((Listing l) -> l.getQuantityRemaining()).reversed());

    PriorityQueue<Listing> needQueue = new PriorityQueue<>(byNeedPriority);
    PriorityQueue<Listing> surplusQueue = new PriorityQueue<>(bySurplusPriority);

    //submitting a listing
    public List<MatchLine> submit(Listing listing){
        List<MatchLine> matches = new ArrayList<>();

        if(listing.getType() == ListingType.NEED){
          matches = attemptMatch(listing);

           if(listing.getQuantityRemaining() > 0) {
               needQueue.add(listing);
           }
        }else if (listing.getType() == ListingType.SURPLUS) {
           matches = attemptMatch(listing);

            if(listing.getQuantityRemaining() > 0) {
            surplusQueue.add(listing);
        }
        }

        return matches;
    }

    //matching a listing
    private List<MatchLine> attemptMatch(Listing listing) {
        List<MatchLine> matches = new ArrayList<>();

        PriorityQueue<Listing> opposite = (listing.getType() == ListingType.NEED)
                ? surplusQueue : needQueue;

        List<Listing> skipped = new ArrayList<>();

        while(listing.getQuantityRemaining() > 0 && !opposite.isEmpty()) {
          Listing candidate = opposite.poll();

          boolean sameCategory = listing.getCategoryId() == candidate.getCategoryId();

          boolean compatible = (listing.getType() == ListingType.NEED)
                  ? datesAreCompatible(listing.getNeededBy(), candidate.getAvailableUntil())
                  : datesAreCompatible(candidate.getNeededBy(), listing.getAvailableUntil());

//continuing tomorrow
          if(!sameCategory || !compatible){
            skipped.add(candidate);
          }else{
           int matchedQuantity = Math.min(listing.getQuantityRemaining(), candidate.getQuantityRemaining() );
           listing.setQuantityRemaining(listing.getQuantityRemaining() - matchedQuantity);
           candidate.setQuantityRemaining(candidate.getQuantityRemaining() - matchedQuantity);

           int needListingId = (listing.getType() == ListingType.NEED) ? listing.getListingId() : candidate.getListingId();
           int surplusListingId = (listing.getType() == ListingType.SURPLUS) ?  listing.getListingId() : candidate.getListingId();

           MatchLine match = new MatchLine(0,needListingId,surplusListingId,matchedQuantity,LocalDateTime.now());
           matches.add(match);

           if(candidate.getQuantityRemaining() > 0) {
               skipped.add(candidate);
           }
          }
      }
        opposite.addAll(skipped);
        return matches;
    }

    //checking which listing has cross the deadline
    public boolean datesAreCompatible(LocalDate neededBy, LocalDate availableUntil){
        if (availableUntil.isAfter(neededBy) || availableUntil.isEqual(neededBy)) {
            return true;
        } else {
            return false;
        }
    }

    // method for expiration
    //  expire(LocalDateTime expired)
}