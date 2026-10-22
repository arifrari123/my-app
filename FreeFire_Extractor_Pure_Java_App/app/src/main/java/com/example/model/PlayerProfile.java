package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class PlayerProfile {
    private final long accountId;
    private final String guestUid;
    private final String nickname;
    private final int level;
    private final long exp;
    private final long nextLevelExp;
    private final String region;
    private final String clanName;
    private final String signature;
    private final int creditScore;

    // BR Rank
    private final String brRankTitle;
    private final int brSeason;
    private final long brRankPoints;
    private final String brMaxRankTitle;

    // CS Rank
    private final String csRankTitle;
    private final int csSeason;
    private final long csProtectionPoints;

    // Equipment & Loadout
    private final long activeCharacterId;
    private final String activeCharacterName;
    private final long petId;
    private final String petName;
    private final String petSpecies;
    private final int petLevel;
    private final List<Long> clothesIds;

    // Timestamps & Meta
    private final String accountCreatedDate;
    private final String lastLoginTimestamp;
    private final String rawJwtToken;
    private final boolean isDirectJwtAuth;

    public PlayerProfile(long accountId, String guestUid, String nickname, int level, long exp,
                         long nextLevelExp, String region, String clanName, String signature,
                         int creditScore, String brRankTitle, int brSeason, long brRankPoints,
                         String brMaxRankTitle, String csRankTitle, int csSeason,
                         long csProtectionPoints, long activeCharacterId, String activeCharacterName,
                         long petId, String petName, String petSpecies, int petLevel,
                         List<Long> clothesIds, String accountCreatedDate, String lastLoginTimestamp,
                         String rawJwtToken, boolean isDirectJwtAuth) {
        this.accountId = accountId;
        this.guestUid = guestUid;
        this.nickname = nickname;
        this.level = level;
        this.exp = exp;
        this.nextLevelExp = nextLevelExp;
        this.region = region;
        this.clanName = clanName;
        this.signature = signature;
        this.creditScore = creditScore;
        this.brRankTitle = brRankTitle;
        this.brSeason = brSeason;
        this.brRankPoints = brRankPoints;
        this.brMaxRankTitle = brMaxRankTitle;
        this.csRankTitle = csRankTitle;
        this.csSeason = csSeason;
        this.csProtectionPoints = csProtectionPoints;
        this.activeCharacterId = activeCharacterId;
        this.activeCharacterName = activeCharacterName;
        this.petId = petId;
        this.petName = petName;
        this.petSpecies = petSpecies;
        this.petLevel = petLevel;
        this.clothesIds = clothesIds != null ? clothesIds : new ArrayList<>();
        this.accountCreatedDate = accountCreatedDate;
        this.lastLoginTimestamp = lastLoginTimestamp;
        this.rawJwtToken = rawJwtToken;
        this.isDirectJwtAuth = isDirectJwtAuth;
    }

    public long getAccountId() { return accountId; }
    public String getGuestUid() { return guestUid; }
    public String getNickname() { return nickname; }
    public int getLevel() { return level; }
    public long getExp() { return exp; }
    public long getNextLevelExp() { return nextLevelExp; }
    public String getRegion() { return region; }
    public String getClanName() { return clanName; }
    public String getSignature() { return signature; }
    public int getCreditScore() { return creditScore; }
    public String getBrRankTitle() { return brRankTitle; }
    public int getBrSeason() { return brSeason; }
    public long getBrRankPoints() { return brRankPoints; }
    public String getBrMaxRankTitle() { return brMaxRankTitle; }
    public String getCsRankTitle() { return csRankTitle; }
    public int getCsSeason() { return csSeason; }
    public long getCsProtectionPoints() { return csProtectionPoints; }
    public long getActiveCharacterId() { return activeCharacterId; }
    public String getActiveCharacterName() { return activeCharacterName; }
    public long getPetId() { return petId; }
    public String getPetName() { return petName; }
    public String getPetSpecies() { return petSpecies; }
    public int getPetLevel() { return petLevel; }
    public List<Long> getClothesIds() { return clothesIds; }
    public String getAccountCreatedDate() { return accountCreatedDate; }
    public String getLastLoginTimestamp() { return lastLoginTimestamp; }
    public String getRawJwtToken() { return rawJwtToken; }
    public boolean isDirectJwtAuth() { return isDirectJwtAuth; }
}
