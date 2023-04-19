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
import java.util.concurrent.CompletableFuture;

public abstract class RedisAPI<DTO> {
    static Main main = Main.getInstance();
    static protected RedisClient client = RedisClient.create(main.getServerConfig().getRedisUrl());
    static protected StatefulRedisConnection<String, String> connection = client.connect();
    static protected StatefulRedisPubSubConnection<String, String> pubSubConnection = client.connectPubSub();
    static protected RedisCommands<String, String> syncCommands = connection.sync();
    static protected RedisPubSubCommands<String, String> pubSubCommands;
    private final Class<DTO> dtoType;
    private final String redisKey;

    public RedisAPI(
            String redisKey,
            Class<DTO> dtoType
    ) {
        this.redisKey = redisKey;
        this.dtoType = dtoType;
    }

    public CompletableFuture<DTO> get(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final boolean inDebugMode = main.getServerConfig().isInDebugMode();
                final String data = syncCommands.get(redisKey + key);
                if (inDebugMode) {
                    Log.info("");
                    Log.info("REDIS: getCache is called with key: " + key);

                    if (data == null || data.length() < 1) {
                        Log.warning("Result is null or length is < 1");
                    } else {
                        Log.info("Result: " + (data.length() > 250 ? data.substring(0, 250) + "... (+" + (data.length() - 250) + " lines)" : data));
                    }
                    Log.info("");
                }

                if(data == null) throw new Exception("Redis data is null");

                JsonElement jsonElement = data.length() < 1 ? JsonNull.INSTANCE.getAsJsonNull() :
                        JsonParser.parseString(data).getAsJsonObject();

                return Main.gson.fromJson(jsonElement, dtoType);
            } catch (Exception e) {
                return null;
            }
        });
    }

    public CompletableFuture<Boolean> set(String key, DTO data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final boolean inDebugMode = main.getServerConfig().isInDebugMode();
                final String result = syncCommands.set(redisKey + key, Main.gson.toJson(data));
                if (inDebugMode) {
                    Log.info("");
                    Log.info("REDIS: setCache is called with key: " + key);
                    Log.info("Result: " + (result.length() > 250 ? result.substring(0, 250) + "... (+" + (result.length() - 250) + " lines)" : result));
                    Log.info("");
                }
                return result.equals("OK");
            } catch (Exception e) {
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> exists(String key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final boolean inDebugMode = main.getServerConfig().isInDebugMode();
                final Long amount = syncCommands.exists(redisKey + key);
                final boolean exists = amount > 0;
                if (inDebugMode) {
                    Log.info("");
                    Log.info("REDIS: existsInCache: " + key);
                    Log.info("Result: " + exists);
                    Log.info("");
                }
                return exists;
            } catch (Exception e) {
                return false;
            }
        });
    }

    public static void setListeners(ArrayList<RedisPubSubListener<String, String>> listeners) {
        listeners.forEach((RedisPubSubListener<String, String> k) -> {
            RedisAPI.pubSubConnection.addListener(k);
        });

        RedisAPI.pubSubCommands = pubSubConnection.sync();
    }

    public static void setSubscriptions(ArrayList<String> subscriptions) {
        subscriptions.forEach((String k) -> {
            RedisAPI.pubSubCommands.subscribe(k);
        });
    }

    public static void connect(String url) {
        client = RedisClient.create(main.getServerConfig().getRedisUrl());
        connection = client.connect();
        pubSubConnection = client.connectPubSub();
        syncCommands = connection.sync();
    }

    public static RedisClient getClient() {
        return client;
    }

    public static StatefulRedisConnection<String, String> getConnection() {
        return connection;
    }
}
