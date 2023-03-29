package com.fendyk.configs;

import com.fendyk.Main;
import de.leonhard.storage.Toml;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class ServerConfig {

    Toml config;
    final String worldName;
    final boolean inDebugMode;

    final String redisUrl;
    final String apiUrl;
    final String jwtToken;

    final Location spawnLocation;

    public ServerConfig() {
        config = new Toml("config", "plugins/QuantaServer");
        this.inDebugMode = config.getOrSetDefault("isInDebugMode", false);
        this.worldName = config.getOrSetDefault("worldName", "world");

        double x = config.getOrSetDefault("spawnLocation.x", 0);
        double y = config.getOrSetDefault("spawnLocation.y", 120);
        double z = config.getOrSetDefault("spawnLocation.z", 0);

        this.spawnLocation = new Location(Bukkit.getWorld(worldName), x,y,z, 0, 0);

        this.redisUrl = config.getOrSetDefault("redisUrl", "<url>");
        this.apiUrl = config.getOrSetDefault("apiUrl", "<url>");
        this.jwtToken = config.getOrSetDefault("jwtToken", "<token>");
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
}