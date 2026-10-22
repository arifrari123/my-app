package com.example.model;

public class TokenReport {
    private final String rawToken;
    private final long accountId;
    private final String region;
    private final String countryCode;
    private final long expTimestamp;
    private final long creationTimestamp;
    private final long lastLoginTimestamp;
    private final String expirationDateFormatted;
    private final String creationDateFormatted;
    private final String lastLoginDateFormatted;
    private final long remainingSeconds;
    private final int validityPercent;
    private final boolean isExpired;

    public TokenReport(String rawToken, long accountId, String region, String countryCode,
                       long expTimestamp, long creationTimestamp, long lastLoginTimestamp,
                       String expirationDateFormatted, String creationDateFormatted,
                       String lastLoginDateFormatted, long remainingSeconds,
                       int validityPercent, boolean isExpired) {
        this.rawToken = rawToken;
        this.accountId = accountId;
        this.region = region;
        this.countryCode = countryCode;
        this.expTimestamp = expTimestamp;
        this.creationTimestamp = creationTimestamp;
        this.lastLoginTimestamp = lastLoginTimestamp;
        this.expirationDateFormatted = expirationDateFormatted;
        this.creationDateFormatted = creationDateFormatted;
        this.lastLoginDateFormatted = lastLoginDateFormatted;
        this.remainingSeconds = remainingSeconds;
        this.validityPercent = validityPercent;
        this.isExpired = isExpired;
    }

    public String getRawToken() { return rawToken; }
    public long getAccountId() { return accountId; }
    public String getRegion() { return region; }
    public String getCountryCode() { return countryCode; }
    public long getExpTimestamp() { return expTimestamp; }
    public long getCreationTimestamp() { return creationTimestamp; }
    public long getLastLoginTimestamp() { return lastLoginTimestamp; }
    public String getExpirationDateFormatted() { return expirationDateFormatted; }
    public String getCreationDateFormatted() { return creationDateFormatted; }
    public String getLastLoginDateFormatted() { return lastLoginDateFormatted; }
    public long getRemainingSeconds() { return remainingSeconds; }
    public int getValidityPercent() { return validityPercent; }
    public boolean isExpired() { return isExpired; }
}
