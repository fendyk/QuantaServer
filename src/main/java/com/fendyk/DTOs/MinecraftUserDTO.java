package com.fendyk.DTOs;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;

public class MinecraftUserDTO {

    String userId;
    Date authorizeExpireDate;
    Float quanta;
    ArrayList<LocationDTO> homes;
    ArrayList<String> memberLandIDs;

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

    public ArrayList<LocationDTO> getHomes() {
        return homes;
    }

    public void setHomes(ArrayList<LocationDTO> homes) {
        this.homes = homes;
    }

    public ArrayList<String> getMemberLandIDs() {
        return memberLandIDs;
    }

    public void setMemberLandIDs(ArrayList<String> memberLandIDs) {
        this.memberLandIDs = memberLandIDs;
    }
}
