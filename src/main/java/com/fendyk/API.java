package com.fendyk;

import com.fendyk.clients.FetchAPI;
import com.fendyk.clients.RedisAPI;
import com.fendyk.clients.apis.ChunkAPI;
import com.fendyk.clients.apis.LandAPI;
import com.fendyk.clients.apis.MinecraftUserAPI;
import com.fendyk.clients.fetch.FetchChunk;
import com.fendyk.clients.fetch.FetchLand;
import com.fendyk.clients.fetch.FetchMinecraftUser;
import com.fendyk.clients.redis.RedisChunk;
import com.fendyk.clients.redis.RedisLand;
import com.fendyk.clients.redis.RedisMinecraftUser;
import de.leonhard.storage.Toml;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class API {
    final String worldName;
    final boolean inDebugMode;
    private final RedisClient client;
    Main server;
    MinecraftUserAPI minecraftUserAPI;
    LandAPI landAPI;
    ChunkAPI chunkAPI;

    FetchAPI<String, Object,  Object> fetchAPI;
    RedisAPI<String, Object> redisAPI;

    public API(Main server, Toml config, ArrayList<RedisPubSubListener<String, String>> listeners, HashMap<String, String> subscriptions) {
        this.server = server;
        this.inDebugMode = config.getOrSetDefault("isInDebugMode", false);
        this.worldName = config.getOrSetDefault("worldName", "overworld");

        String redisUrl = config.getOrSetDefault("redisUrl", "<url>");
        String apiUrl = config.getOrSetDefault("apiUrl", "<url>");

        this.client = RedisClient.create(redisUrl);

        /* Sometimes we need to access certain api methods like redis's pubsub commands */
        redisAPI = new RedisAPI<>(server, client, inDebugMode, listeners, subscriptions) {
            @Override
            public @Nullable Object get(String key) {return null;}
            @Override
            public boolean set(String key, Object data) {return false;}

            @Override
            public boolean exists(String key) {
                return false;
            }
        };

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

    public FetchAPI<String, Object, Object> getFetchAPI() {return fetchAPI;}
    public RedisAPI<String, Object> getRedisAPI() {return redisAPI;}
    public MinecraftUserAPI getMinecraftUserAPI() {return this.minecraftUserAPI;}
    public LandAPI getLandAPI() {return this.landAPI;}
    public ChunkAPI getChunkAPI() {return this.chunkAPI;}

}
