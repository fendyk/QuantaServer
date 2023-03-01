package com.fendyk.DTOs;

import java.util.ArrayList;

public class ActivitiesDTO {

    public String id;
    public String minecraftUserId;

    public ArrayList<ActivityDTO> mining;
    public ArrayList<ActivityDTO> pvp;
    public ArrayList<ActivityDTO> pve;

    public String getId() {
        return id;
    }

    public String getMinecraftUserId() {
        return minecraftUserId;
    }

    public ArrayList<ActivityDTO> getMining() {
        return mining;
    }

    public ArrayList<ActivityDTO> getPvp() {
        return pvp;
    }

    public ArrayList<ActivityDTO> getPve() {
        return pve;
    }
}
