package server.shareboard.domain;

import java.time.LocalDateTime;

public class MatchHistoryEntry {
    private int matchLineId;
    private int counterpartListingId;
    private String counterpartUsername;
    private boolean counterpartIsOrganization;
    private String counterpartRole; // "NEED" or "SURPLUS":  the counterpart's role
    private int quantityMatched;
    private LocalDateTime matchedAt;

    public MatchHistoryEntry() {
    }

    public MatchHistoryEntry(int matchLineId, int counterpartListingId, String counterpartUsername,
                             boolean counterpartIsOrganization, String counterpartRole,
                             int quantityMatched, LocalDateTime matchedAt) {
        this.matchLineId = matchLineId;
        this.counterpartListingId = counterpartListingId;
        this.counterpartUsername = counterpartUsername;
        this.counterpartIsOrganization = counterpartIsOrganization;
        this.counterpartRole = counterpartRole;
        this.quantityMatched = quantityMatched;
        this.matchedAt = matchedAt;
    }

    public int getMatchLineId() {
        return matchLineId;
    }

    public void setMatchLineId(int matchLineId) {
        this.matchLineId = matchLineId;
    }

    public int getCounterpartListingId() {
        return counterpartListingId;
    }

    public void setCounterpartListingId(int counterpartListingId) {
        this.counterpartListingId = counterpartListingId;
    }

    public String getCounterpartUsername() {
        return counterpartUsername;
    }

    public void setCounterpartUsername(String counterpartUsername) {
        this.counterpartUsername = counterpartUsername;
    }

    public boolean isCounterpartIsOrganization() {
        return counterpartIsOrganization;
    }

    public void setCounterpartIsOrganization(boolean counterpartIsOrganization) {
        this.counterpartIsOrganization = counterpartIsOrganization;
    }

    public String getCounterpartRole() {
        return counterpartRole;
    }

    public void setCounterpartRole(String counterpartRole) {
        this.counterpartRole = counterpartRole;
    }

    public int getQuantityMatched() {
        return quantityMatched;
    }

    public void setQuantityMatched(int quantityMatched) {
        this.quantityMatched = quantityMatched;
    }

    public LocalDateTime getMatchedAt() {
        return matchedAt;
    }

    public void setMatchedAt(LocalDateTime matchedAt) {
        this.matchedAt = matchedAt;
    }
}