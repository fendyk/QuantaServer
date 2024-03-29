package com.fendyk.DTOs;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class ChunkDTO {

    public static enum State {
        BLACKLISTED,
        UNCLAIMABLE,
        UNCLAIMED,
        CLAIMED_EXPIRABLE,
        CLAIMED_PERMANENT,
    }

    String id;

    Boolean isClaimable;
    Boolean canExpire;
    int x;
    int z;
    String landId;

    String expirationDate;

    ArrayList<BlacklistedBlockDTO> blacklistedBlocks;

    public ChunkDTO() {
        this.blacklistedBlocks = new ArrayList<>();
    }

    public ArrayList<BlacklistedBlockDTO> getBlacklistedBlocks() {
        return blacklistedBlocks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean isClaimable() {
        return isClaimable;
    }

    public void setClaimable(boolean claimable) {
        isClaimable = claimable;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getLandId() {
        return landId;
    }

    public void setLandId(String landId) {
        this.landId = landId;
    }

    public Boolean canExpire() {return canExpire;}

    public DateTime getExpirationDate() {return new DateTime(expirationDate);}
}
