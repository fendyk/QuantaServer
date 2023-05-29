package com.fendyk;

import com.fendyk.DTOs.MinecraftUserDTO;
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
import com.fendyk.utilities.Log;
import de.leonhard.storage.Toml;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class API {
    Main main = Main.getInstance();
    ActivitiesAPI activitiesAPI;
    MinecraftUserAPI minecraftUserAPI;
    LandAPI landAPI;
    ChunkAPI chunkAPI;

    public API(Main server) {
        ServerConfig serverConfig = main.getServerConfig();
        String redisUrl = serverConfig.getRedisUrl();
        String apiUrl = serverConfig.getApiUrl();
        String jwtToken = serverConfig.getJwtToken();

        ArrayList<RedisPubSubListener<String, String>> listeners = new ArrayList<>();
        listeners.add(new AuthenticationListener(server));
        listeners.add(new ChunkListener(server));
        listeners.add(new LandListener(server));

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

        if(!RedisAPI.getConnection().isOpen()) {
            Log.error("Could not connect to redis, is the server offline?");
        }
        Log.success("Connection to the redis server has been successful!");

        activitiesAPI = new ActivitiesAPI(
                this,
                new FetchActivities("/activities"),
                new RedisActivities("activities:")
        );

        minecraftUserAPI = new MinecraftUserAPI(
                this,
                new FetchMinecraftUser("/minecraftusers"),
                new RedisMinecraftUser("minecraftuser:")
        );

        landAPI = new LandAPI(
                this,
                new FetchLand("/lands"),
                new RedisLand("land:")
        );

        chunkAPI = new ChunkAPI(
                this,
                new FetchChunk("/chunks"),
                new RedisChunk("chunk:")
        );

    }

    public ActivitiesAPI getActivitiesAPI() {return activitiesAPI;}
    public MinecraftUserAPI getMinecraftUserAPI() {return this.minecraftUserAPI;}
    public LandAPI getLandAPI() {return this.landAPI;}
    public ChunkAPI getChunkAPI() {return this.chunkAPI;}

}
