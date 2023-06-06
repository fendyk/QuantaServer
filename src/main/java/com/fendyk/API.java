package com.fendyk;

import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.FetchAPI;
import com.fendyk.clients.RedisAPI;
import com.fendyk.clients.apis.*;
import com.fendyk.clients.fetch.FetchActivities;
import com.fendyk.clients.fetch.FetchChunk;
import com.fendyk.clients.fetch.FetchLand;
import com.fendyk.clients.fetch.FetchMinecraftUser;
import com.fendyk.clients.redis.*;
import com.fendyk.configs.ServerConfig;
import com.fendyk.listeners.redis.AuthenticationListener;
import com.fendyk.listeners.redis.ChunkListener;
import com.fendyk.listeners.redis.LandListener;
import com.fendyk.utilities.Log;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.util.ArrayList;

public class API {
    Main main = Main.getInstance();
    ActivitiesAPI activitiesAPI;
    MinecraftUserAPI minecraftUserAPI;
    LandAPI landAPI;
    ChunkAPI chunkAPI;

    TeleportAPI teleportAPI;

    public API() {
        ServerConfig serverConfig = main.getServerConfig();
        String redisUrl = serverConfig.getRedisUrl();
        String apiUrl = serverConfig.getApiUrl();
        String jwtToken = serverConfig.getJwtToken();

        ArrayList<RedisPubSubListener<String, String>> listeners = new ArrayList<>();
        listeners.add(new AuthenticationListener());
        listeners.add(new ChunkListener());
        listeners.add(new LandListener());

        ArrayList<String> subscriptions = new ArrayList<>();
        subscriptions.add("authentication");
        subscriptions.add("chunk");
        subscriptions.add("land");

        // Connect to the REST api
        FetchAPI.connect(apiUrl, jwtToken);

        // Make the connection to redis
        RedisAPI.connect(redisUrl);
        RedisAPI.setListeners(listeners);
        RedisAPI.setSubscriptions(subscriptions);

        ClientAPI.setApi(this); // Set the API to self

        if(!RedisAPI.getConnection().isOpen()) {
            Log.error("Could not connect to redis, is the server offline?");
        }
        Log.success("Connection to the redis server has been successful!");

        activitiesAPI = new ActivitiesAPI(
                new FetchActivities("/activities"),
                new RedisActivities("activities:")
        );

        minecraftUserAPI = new MinecraftUserAPI(
                new FetchMinecraftUser("/minecraftusers"),
                new RedisMinecraftUser("minecraftuser:")
        );

        landAPI = new LandAPI(
                new FetchLand("/lands"),
                new RedisLand("land:")
        );

        chunkAPI = new ChunkAPI(
                new FetchChunk("/chunks"),
                new RedisChunk("chunk:")
        );

        teleportAPI = new TeleportAPI(
                new RedisTeleport("teleport:")
        );

    }

    public void reconnect() {
        ServerConfig serverConfig = main.getServerConfig();
        String redisUrl = serverConfig.getRedisUrl();
        String apiUrl = serverConfig.getApiUrl();
        String jwtToken = serverConfig.getJwtToken();

        // Connect to the REST api
        FetchAPI.connect(apiUrl, jwtToken);

        // Make the connection to redis
        RedisAPI.connect(redisUrl);
    }

    public ActivitiesAPI getActivitiesAPI() {return activitiesAPI;}
    public MinecraftUserAPI getMinecraftUserAPI() {return this.minecraftUserAPI;}
    public LandAPI getLandAPI() {return this.landAPI;}
    public ChunkAPI getChunkAPI() {return this.chunkAPI;}
    public TeleportAPI getTeleportAPI() {return this.teleportAPI;}

}
