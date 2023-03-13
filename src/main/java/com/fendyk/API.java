package com.fendyk;

import com.fendyk.clients.FetchAPI;
import com.fendyk.clients.RedisAPI;
import com.fendyk.clients.apis.*;
import com.fendyk.clients.fetch.FetchActivities;
import com.fendyk.clients.fetch.FetchChunk;
import com.fendyk.clients.fetch.FetchLand;
import com.fendyk.clients.fetch.FetchMinecraftUser;
import com.fendyk.clients.redis.*;
import com.fendyk.configs.ServerConfig;
import com.fendyk.listeners.AuthenticationListener;
import com.fendyk.listeners.ChunkListener;
import com.fendyk.listeners.LandListener;
import de.leonhard.storage.Toml;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class API {
    final String worldName;
    final boolean inDebugMode;
    private final RedisClient client;
    Main server;
    ActivitiesAPI activitiesAPI;
    MinecraftUserAPI minecraftUserAPI;
    LandAPI landAPI;
    ChunkAPI chunkAPI;
    BlacklistedChunkAPI blacklistedChunkAPI;

    FetchAPI<String, Object,  Object> fetchAPI;
    RedisAPI<String, Object> redisAPI;

    public API(Main server) {
        this.server = server;
        ServerConfig serverConfig = server.getServerConfig();
        this.inDebugMode = serverConfig.isInDebugMode();
        this.worldName = serverConfig.getWorldName();

        String redisUrl = serverConfig.getRedisUrl();
        String apiUrl = serverConfig.getApiUrl();
        String jwtToken = serverConfig.getJwtToken();

        this.client = RedisClient.create(redisUrl);

        ArrayList<RedisPubSubListener<String, String>> listeners = new ArrayList<>();
        listeners.add(new AuthenticationListener(server));
        listeners.add(new ChunkListener(server));
        listeners.add(new LandListener(server));

        ArrayList<String> subscriptions = new ArrayList<>();
        subscriptions.add("authentication");
        subscriptions.add("chunk");
        subscriptions.add("land");


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

        blacklistedChunkAPI = new BlacklistedChunkAPI(
                this,
                null,
                new RedisBlacklistedChunk(server, client, inDebugMode, null, null)
        );

        activitiesAPI = new ActivitiesAPI(
                this,
                new FetchActivities(server, apiUrl, inDebugMode, jwtToken),
                new RedisActivities(server, client, inDebugMode, null, null)
        );

        minecraftUserAPI = new MinecraftUserAPI(
                this,
                new FetchMinecraftUser(server, apiUrl, inDebugMode, jwtToken),
                new RedisMinecraftUser(server, client, inDebugMode, null, null)
        );

        landAPI = new LandAPI(
                this,
                new FetchLand(server, apiUrl, inDebugMode, jwtToken),
                new RedisLand(server, client, inDebugMode, null, null)
        );

        chunkAPI = new ChunkAPI(
                this,
                new FetchChunk(server, apiUrl, inDebugMode, jwtToken),
                new RedisChunk(server, client, inDebugMode, null, null)
        );

    }

    public BlacklistedChunkAPI getBlacklistedChunkAPI() {return blacklistedChunkAPI;}
    public ActivitiesAPI getActivitiesAPI() {return activitiesAPI;}
    public FetchAPI<String, Object, Object> getFetchAPI() {return fetchAPI;}
    public RedisAPI<String, Object> getRedisAPI() {return redisAPI;}
    public MinecraftUserAPI getMinecraftUserAPI() {return this.minecraftUserAPI;}
    public LandAPI getLandAPI() {return this.landAPI;}
    public ChunkAPI getChunkAPI() {return this.chunkAPI;}

}
