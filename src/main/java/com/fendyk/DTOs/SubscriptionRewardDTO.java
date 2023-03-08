package com.fendyk.DTOs;

import java.util.ArrayList;
import java.util.Date;

public class SubscriptionRewardDTO {

    Boolean isClaimed;
    double quanta;
    int crateKeys;
    ArrayList<KitDTO> kits;
    Date createdAt;

    public SubscriptionRewardDTO() {
        this.kits = new ArrayList<>();
    }

    public ArrayList<KitDTO> getKits() {
        return kits;
    }

    public void setKits(ArrayList<KitDTO> kits) {
        this.kits = kits;
    }

    public Boolean isClaimed() {
        return isClaimed;
    }

    public void setClaimed(boolean claimed) {
        isClaimed = claimed;
    }

    public double getQuanta() {
        return quanta;
    }

    public void setQuanta(double quanta) {
        this.quanta = quanta;
    }

    public int getCrateKeys() {
        return crateKeys;
    }

    public void setCrateKeys(int crateKeys) {
        this.crateKeys = crateKeys;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
