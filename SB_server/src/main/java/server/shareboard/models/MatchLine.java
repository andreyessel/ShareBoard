package server.shareboard.models;

import java.time.LocalDateTime;

public class MatchLine {
    private int matchLineId;
    private int  needListingId;
    private int surplusListingId;
    private int quantityMatched;
    private LocalDateTime matchedAt;

    public MatchLine(LocalDateTime matchedAt, int quantityMatched, int surplusListingId, int needListingId, int matchLineId) {
        this.matchedAt = matchedAt;
        this.quantityMatched = quantityMatched;
        this.surplusListingId = surplusListingId;
        this.needListingId = needListingId;
        this.matchLineId = matchLineId;
    }

    public int getMatchLineid() {
        return matchLineId;
    }

    public void setMatchLineid(int matchLineId) {
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
