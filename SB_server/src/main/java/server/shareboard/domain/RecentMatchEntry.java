package server.shareboard.domain;

import java.time.LocalDateTime;

public class RecentMatchEntry {
    private int matchLineId;
    private String categoryName;
    private String needUsername;
    private boolean needIsOrganization;
    private String surplusUsername;
    private boolean surplusIsOrganization;
    private int quantityMatched;
    private LocalDateTime matchedAt;

    public RecentMatchEntry() {
    }

    public RecentMatchEntry(int matchLineId, String categoryName, String needUsername, boolean needIsOrganization,
                            String surplusUsername, boolean surplusIsOrganization, int quantityMatched, LocalDateTime matchedAt) {
        this.matchLineId = matchLineId;
        this.categoryName = categoryName;
        this.needUsername = needUsername;
        this.needIsOrganization = needIsOrganization;
        this.surplusUsername = surplusUsername;
        this.surplusIsOrganization = surplusIsOrganization;
        this.quantityMatched = quantityMatched;
        this.matchedAt = matchedAt;
    }

    public int getMatchLineId() { return matchLineId; }
    public void setMatchLineId(int matchLineId) { this.matchLineId = matchLineId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getNeedUsername() { return needUsername; }
    public void setNeedUsername(String needUsername) { this.needUsername = needUsername; }

    public boolean isNeedIsOrganization() { return needIsOrganization; }
    public void setNeedIsOrganization(boolean needIsOrganization) { this.needIsOrganization = needIsOrganization; }

    public String getSurplusUsername() { return surplusUsername; }
    public void setSurplusUsername(String surplusUsername) { this.surplusUsername = surplusUsername; }

    public boolean isSurplusIsOrganization() { return surplusIsOrganization; }
    public void setSurplusIsOrganization(boolean surplusIsOrganization) { this.surplusIsOrganization = surplusIsOrganization; }

    public int getQuantityMatched() { return quantityMatched; }
    public void setQuantityMatched(int quantityMatched) { this.quantityMatched = quantityMatched; }

    public LocalDateTime getMatchedAt() { return matchedAt; }
    public void setMatchedAt(LocalDateTime matchedAt) { this.matchedAt = matchedAt; }
}