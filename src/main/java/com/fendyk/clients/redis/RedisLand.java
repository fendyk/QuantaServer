package com.fendyk.clients.redis;

import com.fendyk.QuantaServer;
import com.fendyk.clients.RedisAPI;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RedisLand extends RedisAPI<UUID> {
    public RedisLand(QuantaServer server,
                      RedisClient client,
                      boolean inDebugMode,
                      ArrayList<RedisPubSubListener<String, String>> listeners,
                      HashMap<String, String> subscriptions) {
        super(server, client, inDebugMode, listeners, subscriptions);
    }

    @Override
    public JsonElement get(UUID key) {
        return getCache("land:" + key.toString());
    }

    @Override
    public boolean set(UUID key, JsonObject data) {
        return setCache("land:" + key.toString(), data);
    }
}
