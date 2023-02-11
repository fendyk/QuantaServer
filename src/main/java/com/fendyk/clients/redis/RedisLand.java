package com.fendyk.clients.redis;

import com.fendyk.QuantaServer;
import com.fendyk.clients.RedisAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.util.ArrayList;
import java.util.UUID;

public class RedisLand extends RedisAPI<UUID> {
    public RedisLand(QuantaServer server, RedisClient client, boolean inDebugMode, ArrayList<RedisPubSubListener<String, String>> listeners) {
        super(server, client, inDebugMode, listeners);
    }

    @Override
    public JsonObject get(UUID key) {
        return JsonParser.parseString(
                syncCommands.get("land:" + key.toString())
        ).getAsJsonObject();
    }

    @Override
    public boolean set(UUID key, JsonObject data) {
        String result = syncCommands.set("land:" + key.toString(), data.toString());
        return result.equals("OK");
    }
}
