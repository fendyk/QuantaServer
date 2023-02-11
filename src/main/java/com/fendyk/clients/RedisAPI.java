package com.fendyk.clients;

import com.fendyk.Log;
import com.fendyk.QuantaServer;
import com.fendyk.listeners.redis.AuthenticationListener;
import com.fendyk.listeners.redis.UserListener;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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

    public RedisAPI(QuantaServer server, RedisClient client, boolean inDebugMode, ArrayList<RedisPubSubListener<String, String>> listeners) {
        this.server = server;
        this.inDebugMode = inDebugMode;

        this.client = client; //TODO:
        this.connection = client.connect();
        this.syncCommands = connection.sync();
        this.pubSubConnection = client.connectPubSub();

        // Add listeners
        listeners.forEach(item -> {
            this.pubSubConnection.addListener(item);
        });

        this.pubSubCommands = pubSubConnection.sync();

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

    public abstract JsonObject get(T key);
    public abstract boolean set(T key, JsonObject data);

    public JsonObject getChunk(Chunk chunk) {
        final int x = chunk.getX();
        final int z = chunk.getZ();
        return JsonParser.parseString(
                syncCommands.get("chunk:" + x + ":" + z)
        ).getAsJsonObject();
    }

    public boolean createLand(UUID owner, String name, Chunk chunk) {
        return false;
    }

    public boolean claimChunkForLand(UUID owner, Chunk chunk) {
        return false;
    }

    public JsonObject getLand(UUID owner) {
        return JsonParser.parseString(
                syncCommands.get("land:" + owner.toString())
        ).getAsJsonObject();
    }

}
