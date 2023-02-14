package com.fendyk.clients.redis;

import com.fendyk.QuantaServer;
import com.fendyk.clients.RedisAPI;
import com.fendyk.utilities.Vector2;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RedisChunk extends RedisAPI<Vector2> {
    public RedisChunk(QuantaServer server,
                      RedisClient client,
                      boolean inDebugMode,
                      ArrayList<RedisPubSubListener<String, String>> listeners,
                      HashMap<String, String> subscriptions) {
        super(server, client, inDebugMode, listeners, subscriptions);
    }

    @Override
    public JsonElement get(Vector2 key) {
        return getCache("chunk:" + key.getX() + ":" + key.getY());
    }

    @Override
    public boolean set(Vector2 key, JsonObject data) {
        return setCache("chunk:" + key.getX() + ":" + key.getY(), data);
    }
}
