package server.shareboard.models;

import java.time.LocalDate;

public class Listing {
    private int listingId;
    private int userId;
    private int categoryId;
    private ListingType type;
    private int quantity;
    private int quantityRemaining;
    private String urgency;
    private LocalDate neededBy;
    private LocalDate availableUntil;
    private String status;
    private LocalDate createdAt;

}
