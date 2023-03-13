package com.fendyk.configs;

import com.fendyk.Main;
import de.leonhard.storage.Toml;

public class ServerConfig {

    Toml config;
    final String worldName;
    final boolean inDebugMode;

    final String redisUrl;
    final String apiUrl;
    final String jwtToken;

    public ServerConfig(Main server) {
        config = new Toml("config", "plugins/QuantaServer");
        this.inDebugMode = config.getOrSetDefault("isInDebugMode", false);
        this.worldName = config.getOrSetDefault("worldName", "world");

        this.redisUrl = config.getOrSetDefault("redisUrl", "<url>");
        this.apiUrl = config.getOrSetDefault("apiUrl", "<url>");
        this.jwtToken = config.getOrSetDefault("jwtToken", "<token>");
    }

    public Toml getConfig() {
        return config;
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