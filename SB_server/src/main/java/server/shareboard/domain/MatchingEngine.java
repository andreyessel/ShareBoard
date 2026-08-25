package server.shareboard.domain;

import server.shareboard.models.Listing;
import server.shareboard.models.ListingType;
import server.shareboard.models.MatchLine;

import java.time.LocalDate;
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

        return List.of();
    }

    //checking which listing has cross the deadline
    public boolean deadlineCrosses(LocalDate neededBy, LocalDate availableUntil){
        if (availableUntil.isBefore(neededBy)) {
            return true;
        } else {
            return false;
        }
    }
}