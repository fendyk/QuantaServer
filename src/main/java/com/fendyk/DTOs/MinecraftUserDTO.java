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
    LocationDTO lastLocation;

    public MinecraftUserDTO() {
        this.homes = new ArrayList<>();
        this.memberLandIDs = new ArrayList<>();
        this.subscriptionRewards = new ArrayList<>();
    }

    public ArrayList<SubscriptionRewardDTO> getSubscriptionRewards() {return subscriptionRewards;}


    public String getId() {
        return id;
    }


    public String getUserId() {
        return userId;
    }


    public Date getAuthorizeExpireDate() {
        return authorizeExpireDate;
    }


    public Float getQuanta() {
        return quanta;
    }


    public ArrayList<TaggedLocationDTO> getHomes() {
        return homes;
    }


    public ArrayList<String> getMemberLandIDs() {
        return memberLandIDs;
    }

    public LocationDTO getLastLocation() {
        return lastLocation;
    }
}
