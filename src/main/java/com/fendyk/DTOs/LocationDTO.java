package com.fendyk.DTOs;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationDTO {
    String world;
    double x;
    double y;
    double z;
    double yaw;
    double pitch;

    public LocationDTO() {

    }

    public LocationDTO(Location location) {
        this.world = location.getWorld().getName();
        this.x = location.x();
        this.y = location.getY();
        this.z = location.getZ();
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }
    public String getWorld() {return world;}

    public void setWorld(String world) {this.world = world;}

    public static Location toLocation(LocationDTO locationDTO) {
        return new Location(
                Bukkit.getWorld(locationDTO.getWorld()),
                locationDTO.getX(),
                locationDTO.getY(),
                locationDTO.getZ(),
                (float) locationDTO.getYaw(),
                (float) locationDTO.getPitch()
        );
    }
}
