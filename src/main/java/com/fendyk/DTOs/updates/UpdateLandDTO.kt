package com.fendyk.DTOs.updates;

import com.fendyk.DTOs.LocationDTO;
import com.fendyk.DTOs.TaggedLocationDTO;

import java.util.ArrayList;

public class UpdateLandDTO {

    public static class MemberDTO {
        String id;
        public MemberDTO(String id) {
            this.id = id;
        }
    }

    String name;
    ArrayList<MemberDTO> connectMembers;
    ArrayList<MemberDTO> disconnectMembers;
    ArrayList<TaggedLocationDTO> pushHomes;
    ArrayList<String> spliceHomes;

    public UpdateLandDTO() {
        this.connectMembers = new ArrayList<>();
        this.disconnectMembers = new ArrayList<>();
        this.pushHomes = new ArrayList<>();
        this.spliceHomes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<MemberDTO> getConnectMembers() {
        return connectMembers;
    }

    public void setConnectMembers(ArrayList<MemberDTO> connectMembers) {
        this.connectMembers = connectMembers;
    }

    public ArrayList<MemberDTO> getDisconnectMembers() {
        return disconnectMembers;
    }

    public void setDisconnectMembers(ArrayList<MemberDTO> disconnectMembers) {
        this.disconnectMembers = disconnectMembers;
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
}
