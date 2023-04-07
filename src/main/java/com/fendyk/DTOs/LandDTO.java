package com.fendyk.DTOs;

import java.util.ArrayList;

public class LandDTO {
    String id;
    String name;
    ArrayList<String> memberIDs;
    String ownerId;
    ArrayList<TaggedLocationDTO> homes;

    ArrayList<ChunkDTO> chunks;

    public LandDTO() {
        memberIDs = new ArrayList<>();
        homes = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getMemberIDs() {
        return memberIDs;
    }

    public void setMemberIDs(ArrayList<String> memberIDs) {
        this.memberIDs = memberIDs;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public ArrayList<TaggedLocationDTO> getHomes() {
        return homes;
    }

    public void setHomes(ArrayList<TaggedLocationDTO> homes) {
        this.homes = homes;
    }

    public ArrayList<ChunkDTO> getChunks() {return chunks;}
}
