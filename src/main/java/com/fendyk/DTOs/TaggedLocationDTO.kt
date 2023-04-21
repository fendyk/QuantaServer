package com.fendyk.DTOs;

import org.bukkit.Location;

public class TaggedLocationDTO {
    String name;
    LocationDTO location;

    public TaggedLocationDTO() {}

    public TaggedLocationDTO(String name, Location location) {
        this.name = name;
        this.location = new LocationDTO(location);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }
}
