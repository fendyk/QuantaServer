package com.fendyk.clients;

import com.fendyk.Log;
import com.fendyk.QuantaServer;
import com.fendyk.listeners.redis.AuthenticationListener;
import com.fendyk.listeners.redis.UserListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class RedisAPI<T> {

    protected final boolean inDebugMode;
    protected QuantaServer server;
    protected RedisClient client;
    protected StatefulRedisConnection<String, String> connection;
    protected StatefulRedisPubSubConnection<String, String> pubSubConnection;
    protected RedisCommands<String, String> syncCommands;
    protected RedisPubSubCommands<String, String> pubSubCommands;

    public RedisAPI(QuantaServer server,
                    RedisClient client,
                    boolean inDebugMode,
                    ArrayList<RedisPubSubListener<String, String>> listeners,
                    HashMap<String, String> subscriptions
    ) {
        this.server = server;
        this.inDebugMode = inDebugMode;

        this.client = client;
        this.connection = client.connect();
        this.syncCommands = connection.sync();
        this.pubSubConnection = client.connectPubSub();

        // Add listeners
        listeners.forEach(item -> {
            this.pubSubConnection.addListener(item);
        });

        this.pubSubCommands = pubSubConnection.sync();

        // Add subscriptions
        subscriptions.forEach((k, v) -> {
            this.pubSubCommands.subscribe(k, v);
        });

        if(!connection.isOpen()) {
            Bukkit.getLogger().info(Log.Error("Redis connection is not open!"));
        }
        else {
            Bukkit.getLogger().info(Log.Success("Redis connection success!"));
        }

    }

    public RedisClient getClient() {return this.client;}
    public StatefulRedisConnection<String, String> getConnection() {
        return connection;
    }
    public StatefulRedisPubSubConnection<String, String> getPubSubConnection() {
        return pubSubConnection;
    }
    public RedisCommands<String, String> getSyncCommands() {
        return syncCommands;
    }

    public RedisPubSubCommands<String, String> getPubSubCommands() {return pubSubCommands;}

    @Nullable
    public abstract JsonElement get(T key);
    public abstract boolean set(T key, JsonObject data);

    public JsonElement getCache(String key) {
        String result = syncCommands.get(key);
        if(inDebugMode) {
            Bukkit.getLogger().info("-----------------------------------");
            Bukkit.getLogger().info("Redis Action GET:");

            if(result == null || result.length() < 1) {
                Bukkit.getLogger().info("Result: Redis Result is null or empty");
            }
            else {
                Bukkit.getLogger().info(result);
            }
            Bukkit.getLogger().info("-----------------------------------");
        }

        return result == null ? null : result.length() < 1 ? JsonNull.INSTANCE.getAsJsonNull() :
                JsonParser.parseString(result).getAsJsonObject();
    }

    public boolean setCache(String key, JsonObject data) {
        String result = syncCommands.set(key, data.toString());
        if(inDebugMode) {
            Bukkit.getLogger().info("Redis Action SET:");
            Bukkit.getLogger().info(result);
        }
        return result.equals("OK");
    }

}
