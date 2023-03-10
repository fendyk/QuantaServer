package com.fendyk.DTOs.updates;

import com.fendyk.DTOs.BlacklistedBlockDTO;
import com.fendyk.DTOs.ChunkDTO;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class UpdateChunkDTO {

    boolean isClaimable;

    String landId;

    ArrayList<BlacklistedBlockDTO> pushBlacklistedBlocks;
    ArrayList<BlacklistedBlockDTO> spliceBlacklistedBlocks;

    public UpdateChunkDTO() {
        this.pushBlacklistedBlocks = new ArrayList<>();
        this.spliceBlacklistedBlocks = new ArrayList<>();
    }

    public ArrayList<BlacklistedBlockDTO> getPushBlacklistedBlocks() {
        return pushBlacklistedBlocks;
    }

    public ArrayList<BlacklistedBlockDTO> getSpliceBlacklistedBlocks() {
        return spliceBlacklistedBlocks;
    }

    public boolean isClaimable() {
        return isClaimable;
    }

    public void setClaimable(boolean claimable) {
        isClaimable = claimable;
    }

    public String getLandId() {
        return landId;
    }

    public void setLandId(String landId) {
        this.landId = landId;
    }
}
