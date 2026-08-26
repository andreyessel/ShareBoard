package server.shareboard.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Listing {
    private int listingId;
    private int userId;
    private int categoryId;
    private ListingType type;
    private int quantity;
    private int quantityRemaining;
    private Urgency urgency;
    private LocalDate neededBy;
    private LocalDate availableUntil;
    private Status status;
    private LocalDateTime createdAt;


    public Listing(int listingId, int userId, int categoryId, ListingType type, int quantity, int quantityRemaining, Urgency urgency, LocalDate neededBy, LocalDate availableUntil, Status status, LocalDateTime createdAt) {
        this.listingId = listingId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.type = type;
        this.quantity = quantity;
        this.quantityRemaining = quantityRemaining;
        this.urgency = urgency;
        this.neededBy = neededBy;
        this.availableUntil = availableUntil;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getListingId() {
        return listingId;
    }

    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public ListingType getType() {
        return type;
    }

    public void setType(ListingType type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantityRemaining() {
        return quantityRemaining;
    }

    public void setQuantityRemaining(int quantityRemaining) {
        this.quantityRemaining = quantityRemaining;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    public LocalDate getNeededBy() {
        return neededBy;
    }

    public void setNeededBy(LocalDate neededBy) {
        this.neededBy = neededBy;
    }

    public LocalDate getAvailableUntil() {
        return availableUntil;
    }

    public void setAvailableUntil(LocalDate availableUntil) {
        this.availableUntil = availableUntil;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
