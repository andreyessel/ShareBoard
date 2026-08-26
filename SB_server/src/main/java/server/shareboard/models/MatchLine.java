package server.shareboard.models;

import java.time.LocalDateTime;

public class MatchLine {
    private int matchLineId;
    private int  needListingId;
    private int surplusListingId;
    private int quantityMatched;
    private LocalDateTime matchedAt;


    public MatchLine(int matchLineId, int needListingId, int surplusListingId, int quantityMatched, LocalDateTime matchedAt) {
        this.matchLineId = matchLineId;
        this.needListingId = needListingId;
        this.surplusListingId = surplusListingId;
        this.quantityMatched = quantityMatched;
        this.matchedAt = matchedAt;
    }

    public int getMatchLineId() {
        return matchLineId;
    }

    public void setMatchLineId(int matchLineId) {
        this.matchLineId = matchLineId;
    }

    public int getNeedListingId() {
        return needListingId;
    }

    public void setNeedListingId(int needListingId) {
        this.needListingId = needListingId;
    }

    public int getSurplusListingId() {
        return surplusListingId;
    }

    public void setSurplusListingId(int surplusListingId) {
        this.surplusListingId = surplusListingId;
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
