package com.fendyk.clients.redis;

import com.fendyk.QuantaServer;
import com.fendyk.clients.RedisAPI;
import com.fendyk.utilities.Vector2;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.util.ArrayList;

public class RedisChunk extends RedisAPI<Vector2> {
    public RedisChunk(QuantaServer server, RedisClient client, boolean inDebugMode, ArrayList<RedisPubSubListener<String, String>> listeners) {
        super(server, client, inDebugMode, listeners);
    }

    @Override
    public JsonObject get(Vector2 key) {
        return JsonParser.parseString(
                syncCommands.get("chunk:" + key.getX() + ":" + key.getY())
        ).getAsJsonObject();
    }

    @Override
    public boolean set(Vector2 key, JsonObject data) {
        String result = syncCommands.set("chunk:" + key.getX() + ":" + key.getY(), data.toString());
        return result.equals("OK");
    }
}
