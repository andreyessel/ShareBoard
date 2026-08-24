package server.shareboard.models;

import java.time.LocalDate;

public class MatchLine {
    private int matchLineid;
    private int  needListingId;
    private int surplusListingId;
    private int quantityMatched;
    private LocalDate matchedAt;

    public MatchLine(LocalDate matchedAt, int quantityMatched, int surplusListingId, int needListingId, int matchLineid) {
        this.matchedAt = matchedAt;
        this.quantityMatched = quantityMatched;
        this.surplusListingId = surplusListingId;
        this.needListingId = needListingId;
        this.matchLineid = matchLineid;
    }

    public int getMatchLineid() {
        return matchLineid;
    }

    public void setMatchLineid(int matchLineid) {
        this.matchLineid = matchLineid;
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

    public LocalDate getMatchedAt() {
        return matchedAt;
    }

    public void setMatchedAt(LocalDate matchedAt) {
        this.matchedAt = matchedAt;
    }
}
