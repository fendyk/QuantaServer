package com.fendyk.configs;

import com.fendyk.utilities.LocationUtil;
import de.leonhard.storage.Toml;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class ServerConfig {

    final String worldName;
    final boolean inDebugMode;
    final String redisUrl;
    final String apiUrl;
    final String jwtToken;
    final Location spawnLocation;
    Toml config;
    World overworld;
    int blacklistedChunkRadius;

    public ServerConfig() {
        config = new Toml("config", "plugins/QuantaServer");
        this.inDebugMode = config.getOrSetDefault("isInDebugMode", false);
        this.worldName = config.getOrSetDefault("worldName", "world");

        this.overworld = Bukkit.getWorld(worldName);

        double x = config.getOrSetDefault("spawnLocation.x", 0);
        double y = config.getOrSetDefault("spawnLocation.y", 120);
        double z = config.getOrSetDefault("spawnLocation.z", 0);
        this.spawnLocation = new Location(overworld, x, y, z, 0, 0);

        this.redisUrl = config.getOrSetDefault("redisUrl", "<url>");
        this.apiUrl = config.getOrSetDefault("apiUrl", "<url>");
        this.jwtToken = config.getOrSetDefault("jwtToken", "<token>");

        // Times 16 for chunks (chunk = 16x16)
        this.blacklistedChunkRadius = config.getOrSetDefault("blacklistedChunkRadius", 32);
    }

    public Toml getConfig() {
        return config;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public String getWorldName() {
        return worldName;
    }

    public boolean isInDebugMode() {
        return inDebugMode;
    }

    public String getRedisUrl() {
        return redisUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public int getBlacklistedChunkRadius() {
        return blacklistedChunkRadius;
    }

    public int getBlacklistedBlockRadius() {
        return blacklistedChunkRadius * 16;
    }

    public World getOverworld() {
        return overworld;
    }

    public boolean isWithinBlacklistedChunkRadius(Location location) {
        return LocationUtil.isWithinRadius(spawnLocation, location, getBlacklistedBlockRadius());
    }
}