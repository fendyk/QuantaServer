package com.fendyk.DTOs;

import java.util.ArrayList;
import java.util.Date;

public class MinecraftUserDTO {

    String id;
    String userId;
    Date authorizeExpireDate;
    Float quanta;
    ArrayList<TaggedLocationDTO> homes;
    ArrayList<String> memberLandIDs;
    ArrayList<SubscriptionRewardDTO> subscriptionRewards;

    public MinecraftUserDTO() {
        this.homes = new ArrayList<>();
        this.memberLandIDs = new ArrayList<>();
        this.subscriptionRewards = new ArrayList<>();
    }

    public ArrayList<SubscriptionRewardDTO> getSubscriptionRewards() {return subscriptionRewards;}

    public void setSubscriptionRewards(ArrayList<SubscriptionRewardDTO> subscriptionRewards) {this.subscriptionRewards = subscriptionRewards;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getAuthorizeExpireDate() {
        return authorizeExpireDate;
    }

    public void setAuthorizeExpireDate(Date authorizeExpireDate) {
        this.authorizeExpireDate = authorizeExpireDate;
    }

    public Float getQuanta() {
        return quanta;
    }

    public void setQuanta(Float quanta) {
        this.quanta = quanta;
    }

    public ArrayList<TaggedLocationDTO> getHomes() {
        return homes;
    }

    public void setHomes(ArrayList<TaggedLocationDTO> homes) {
        this.homes = homes;
    }

    public ArrayList<String> getMemberLandIDs() {
        return memberLandIDs;
    }

    public void setMemberLandIDs(ArrayList<String> memberLandIDs) {
        this.memberLandIDs = memberLandIDs;
    }
}
