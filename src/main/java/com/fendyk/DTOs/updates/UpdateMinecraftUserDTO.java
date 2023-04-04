package com.fendyk.DTOs.updates;

import com.fendyk.DTOs.SubscriptionRewardDTO;
import com.fendyk.DTOs.TaggedLocationDTO;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;

public class UpdateMinecraftUserDTO {
    double quanta;
    ArrayList<TaggedLocationDTO> pushHomes;
    ArrayList<String> spliceHomes;
    ArrayList<SubscriptionRewardDTO> pushSubscriptionRewards;
    ArrayList<String> spliceSubscriptionRewards;
    ArrayList<String> claimSubscriptionRewards;

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

    public ArrayList<String> getSpliceSubscriptionRewards() {
        return spliceSubscriptionRewards;
    }

    public ArrayList<String> getClaimSubscriptionRewards() {
        return claimSubscriptionRewards;
    }

}
