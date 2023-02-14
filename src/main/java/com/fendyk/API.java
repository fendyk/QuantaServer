package com.fendyk;

import com.fendyk.clients.apis.ChunkAPI;
import com.fendyk.clients.apis.LandAPI;
import com.fendyk.clients.apis.MinecraftUserAPI;
import com.fendyk.clients.fetch.FetchChunk;
import com.fendyk.clients.fetch.FetchLand;
import com.fendyk.clients.fetch.FetchMinecraftUser;
import com.fendyk.clients.redis.RedisChunk;
import com.fendyk.clients.redis.RedisLand;
import com.fendyk.clients.redis.RedisMinecraftUser;
import com.google.gson.JsonObject;
import de.leonhard.storage.Toml;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class API {
    final String worldName;
    final boolean inDebugMode;
    private final RedisClient client;
    QuantaServer server;
    MinecraftUserAPI minecraftUserAPI;
    LandAPI landAPI;
    ChunkAPI chunkAPI;

    public API(QuantaServer server, Toml config, ArrayList<RedisPubSubListener<String, String>> listeners, HashMap<String, String> subscriptions) {
        this.server = server;
        this.inDebugMode = config.getOrSetDefault("isInDebugMode", false);
        this.worldName = config.getOrSetDefault("worldName", "overworld");

        String redisUrl = config.getOrSetDefault("redisUrl", "<url>");
        String apiUrl = config.getOrSetDefault("apiUrl", "<url>");

        this.client = RedisClient.create(redisUrl);

        minecraftUserAPI = new MinecraftUserAPI(
                this,
                new FetchMinecraftUser(server, apiUrl, inDebugMode),
                new RedisMinecraftUser(server, client, inDebugMode, listeners, subscriptions)
        );

        landAPI = new LandAPI(
                this,
                new FetchLand(server, apiUrl, inDebugMode),
                new RedisLand(server, client, inDebugMode, listeners, subscriptions)
        );

        chunkAPI = new ChunkAPI(
                this,
                new FetchChunk(server, apiUrl, inDebugMode),
                new RedisChunk(server, client, inDebugMode, listeners, subscriptions)
        );

    }

    public MinecraftUserAPI getMinecraftUserAPI() {return this.minecraftUserAPI;}
    public LandAPI getLandAPI() {return this.landAPI;}
    public ChunkAPI getChunkAPI() {return this.chunkAPI;}

}
