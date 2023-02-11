package com.fendyk;

import com.fendyk.clients.apis.MinecraftUserAPI;
import com.fendyk.clients.fetch.FetchMinecraftUser;
import com.fendyk.clients.redis.RedisMinecraftUser;
import com.google.gson.JsonObject;
import de.leonhard.storage.Toml;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.util.ArrayList;
import java.util.UUID;

public class API {

    final String worldName;
    final boolean inDebugMode;
    QuantaServer server;
    MinecraftUserAPI minecraftUserAPI;

    public boolean isInDebugMode() {return inDebugMode;}

    public API(QuantaServer server, Toml config, ArrayList<RedisPubSubListener<String, String>> listeners) {
        this.server = server;
        this.inDebugMode = config.getOrSetDefault("isInDebugMode", false);
        this.worldName = config.getOrSetDefault("worldName", "overworld");

        String redisUrl = config.getOrSetDefault("redisUrl", "<url>");
        String apiUrl = config.getOrSetDefault("apiUrl", "<url>");

        RedisClient client = RedisClient.create(redisUrl);

        minecraftUserAPI = new MinecraftUserAPI(
                new FetchMinecraftUser(server, apiUrl, inDebugMode),
                new RedisMinecraftUser(server, client, inDebugMode, listeners)
        );
    }

    public Chunk getChunk(Chunk chunk) {
        JsonObject json = redisAPI.getChunk(chunk) fetchAPI.getChunk(chunk);
        json = json != null ? json : fetchAPI.getChunk(chunk);

        int x = json.get("xCoord").getAsInt();
        int z = json.get("zCoord").getAsInt();

        return Bukkit.getWorld(this.worldName).getChunkAt(x, z);
    }

    public boolean createLand(UUID owner, String name, Chunk chunk) {
        boolean isSet = redisAPI.createLand(owner, name, chunk);
        return isSet || fetchAPI.createLand(owner, name, chunk);
    }

    public boolean claimChunkForLand(UUID owner, Chunk chunk) {
        boolean isSet = redisAPI.claimChunkForLand(owner, chunk);
        return isSet || fetchAPI.claimChunkForLand(owner, chunk);
    }

    public JsonObject getLand(UUID owner) {
        return null;
    }
}
