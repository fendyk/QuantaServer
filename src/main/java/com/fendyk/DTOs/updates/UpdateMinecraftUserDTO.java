package com.fendyk.DTOs.updates;

import com.fendyk.DTOs.SubscriptionRewardDTO;
import com.fendyk.DTOs.TaggedLocationDTO;

import java.util.ArrayList;
import java.util.Date;

public class UpdateMinecraftUserDTO {
    double quanta;
    ArrayList<TaggedLocationDTO> pushHomes;
    ArrayList<String> spliceHomes;
    ArrayList<SubscriptionRewardDTO> pushSubscriptionRewards;
    ArrayList<Date> spliceSubscriptionRewards;
    ArrayList<Date> claimSubscriptionRewards;

    public UpdateMinecraftUserDTO() {
        this.pushHomes = new ArrayList<>();
        this.spliceHomes = new ArrayList<>();
        this.pushSubscriptionRewards = new ArrayList<>();
        this.spliceSubscriptionRewards = new ArrayList<>();
        this.claimSubscriptionRewards = new ArrayList<>();
    }

    public double getQuanta() {
        return quanta;
    }

    public void setQuanta(double quanta) {
        this.quanta = quanta;
    }

    public ArrayList<TaggedLocationDTO> getPushHomes() {
        return pushHomes;
    }

    public void setPushHomes(ArrayList<TaggedLocationDTO> pushHomes) {
        this.pushHomes = pushHomes;
    }

    public ArrayList<String> getSpliceHomes() {
        return spliceHomes;
    }

    public void setSpliceHomes(ArrayList<String> spliceHomes) {
        this.spliceHomes = spliceHomes;
    }

    public ArrayList<SubscriptionRewardDTO> getPushSubscriptionRewards() {
        return pushSubscriptionRewards;
    }

    public void setPushSubscriptionRewards(ArrayList<SubscriptionRewardDTO> pushSubscriptionRewards) {
        this.pushSubscriptionRewards = pushSubscriptionRewards;
    }

    public ArrayList<Date> getSpliceSubscriptionRewards() {
        return spliceSubscriptionRewards;
    }

    public void setSpliceSubscriptionRewards(ArrayList<Date> spliceSubscriptionRewards) {
        this.spliceSubscriptionRewards = spliceSubscriptionRewards;
    }

    public ArrayList<Date> getClaimSubscriptionRewards() {
        return claimSubscriptionRewards;
    }

    public void setClaimSubscriptionRewards(ArrayList<Date> claimSubscriptionRewards) {
        this.claimSubscriptionRewards = claimSubscriptionRewards;
    }
}
