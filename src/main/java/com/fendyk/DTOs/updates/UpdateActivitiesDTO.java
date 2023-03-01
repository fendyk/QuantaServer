package com.fendyk.DTOs.updates;

import com.fendyk.DTOs.ActivityDTO;

import java.util.ArrayList;

public class UpdateActivitiesDTO {

    public ArrayList<ActivityDTO> mining;
    public ArrayList<ActivityDTO> pvp;
    public ArrayList<ActivityDTO> pve;

    public ArrayList<ActivityDTO> getMining() {
        return mining;
    }

    public void setMining(ArrayList<ActivityDTO> mining) {
        this.mining = mining;
    }

    public ArrayList<ActivityDTO> getPvp() {
        return pvp;
    }

    public void setPvp(ArrayList<ActivityDTO> pvp) {
        this.pvp = pvp;
    }

    public ArrayList<ActivityDTO> getPve() {
        return pve;
    }

    public void setPve(ArrayList<ActivityDTO> pve) {
        this.pve = pve;
    }
}
