package com.fendyk.DTOs.updates;

import com.fendyk.DTOs.BlacklistedBlockDTO;
import com.fendyk.DTOs.ChunkDTO;
import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;

public class UpdateChunkDTO {

    Boolean isClaimable;
    Boolean canExpire;
    String landId;
    String expirationDate;
    Boolean resetExpirationDate;
    Boolean resetLandId;

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

    public boolean canExpire() {return canExpire;}

    public void setCanExpire(boolean canExpire) {
        this.canExpire = canExpire;
    }

    public DateTime getExpirationDate() {return new DateTime(expirationDate);}

    public void setExpirationDate(@Nullable DateTime expirationDate) {
        this.expirationDate = expirationDate != null ? expirationDate.toString() : null;
    }

    public Boolean getResetExpirationDate() {
        return resetExpirationDate;
    }

    public void setResetExpirationDate(Boolean resetExpirationDate) {
        this.resetExpirationDate = resetExpirationDate;
    }

    public Boolean getResetLandId() {
        return resetLandId;
    }

    public void setResetLandId(Boolean resetLandId) {
        this.resetLandId = resetLandId;
    }
}
