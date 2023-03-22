package com.fendyk.clients;

import com.fendyk.utilities.Log;
import com.fendyk.Main;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class RedisAPI<K, DTO> {

    protected final boolean inDebugMode;
    protected Main server;
    protected RedisClient client;
    protected StatefulRedisConnection<String, String> connection;
    protected StatefulRedisPubSubConnection<String, String> pubSubConnection;
    protected RedisCommands<String, String> syncCommands;
    protected RedisPubSubCommands<String, String> pubSubCommands;

    public RedisAPI(Main server,
                    RedisClient client,
                    boolean inDebugMode,
                    ArrayList<RedisPubSubListener<String, String>> listeners,
                    ArrayList<String> subscriptions
    ) {
        this.server = server;
        this.inDebugMode = inDebugMode;

        this.client = client;
        this.connection = client.connect();
        this.syncCommands = connection.sync();

        // Add subscriptions
        if (listeners != null && subscriptions != null) {
            this.pubSubConnection = client.connectPubSub();

            listeners.forEach((RedisPubSubListener<String, String> k) -> {
                this.pubSubConnection.addListener(k);
            });

            this.pubSubCommands = pubSubConnection.sync();

            subscriptions.forEach((String k) -> {
                this.pubSubCommands.subscribe(k);
            });
        }

        if (!connection.isOpen()) {
            Log.error("Redis connection is not open.");
        } else {
            Log.success("Redis connection success!");
        }

    }

    public RedisClient getClient() {
        return this.client;
    }

    public StatefulRedisConnection<String, String> getConnection() {
        return connection;
    }

    public StatefulRedisPubSubConnection<String, String> getPubSubConnection() {
        return pubSubConnection;
    }

    public RedisCommands<String, String> getSyncCommands() {
        return syncCommands;
    }

    public RedisPubSubCommands<String, String> getPubSubCommands() {
        return pubSubCommands;
    }

    @Nullable
    public abstract DTO get(K key);

    public abstract boolean set(K key, DTO data);

    public abstract boolean exists(K key);

    public JsonElement getCache(String key) {
        final String result = syncCommands.get(key);
        if (inDebugMode) {
            Log.info("");
            Log.info("REDIS: getCache is called with key: " + key);

            if (result == null || result.length() < 1) {
                Log.warning("Result is null or length is < 1");
            } else {
                Log.info("Result: " + result);
            }
            Log.info("");
        }

        return result == null ? null : result.length() < 1 ? JsonNull.INSTANCE.getAsJsonNull() :
                JsonParser.parseString(result).getAsJsonObject();
    }

    public boolean setCache(String key, String data) {
        final String result = syncCommands.set(key, data);
        if (inDebugMode) {
            Log.info("");
            Log.info("REDIS: setCache is called with key: " + key);
            Log.info("Result: " + result);
            Log.info("");
        }
        return result.equals("OK");
    }

    public boolean existsInCache(String key) {
        final Long amount = syncCommands.exists(key);
        final boolean exists = amount > 0;
        if (inDebugMode) {
            Log.info("");
            Log.info("REDIS: existsInCache: " + key);
            Log.info("Result: " + exists);
            Log.info("");
        }
        return exists;
    }

}
