package com.fendyk.DTOs.updates;

import com.fendyk.DTOs.ChunkDTO;
import com.google.gson.annotations.Expose;

public class UpdateChunkDTO {

    boolean isClaimable;

    String landId;

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
