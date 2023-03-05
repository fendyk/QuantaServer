package com.fendyk.DTOs.updates;

import com.fendyk.DTOs.LocationDTO;

import java.util.ArrayList;

public class UpdateLandDTO {

    String name;
    ArrayList<String> connectMembers;
    ArrayList<String> disconnectMembers;
    ArrayList<LocationDTO> pushHomes;
    ArrayList<LocationDTO> spliceHomes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getConnectMembers() {
        return connectMembers;
    }

    public void setConnectMembers(ArrayList<String> connectMembers) {
        this.connectMembers = connectMembers;
    }

    public ArrayList<String> getDisconnectMembers() {
        return disconnectMembers;
    }

    public void setDisconnectMembers(ArrayList<String> disconnectMembers) {
        this.disconnectMembers = disconnectMembers;
    }

    public ArrayList<LocationDTO> getPushHomes() {
        return pushHomes;
    }

    public void setPushHomes(ArrayList<LocationDTO> pushHomes) {
        this.pushHomes = pushHomes;
    }

    public ArrayList<LocationDTO> getSpliceHomes() {
        return spliceHomes;
    }

    public void setSpliceHomes(ArrayList<LocationDTO> spliceHomes) {
        this.spliceHomes = spliceHomes;
    }
}
