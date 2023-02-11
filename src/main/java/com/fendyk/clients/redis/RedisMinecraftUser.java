package com.fendyk.clients.redis;

import com.fendyk.QuantaServer;
import com.fendyk.clients.RedisAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import okhttp3.Request;

import java.util.ArrayList;
import java.util.UUID;

public class RedisMinecraftUser extends RedisAPI<UUID> {

    public RedisMinecraftUser(QuantaServer server, RedisClient client, boolean inDebugMode, ArrayList<RedisPubSubListener<String, String>> listeners) {
        super(server, client, inDebugMode, listeners);
    }

    public JsonObject get(UUID player) {
        return JsonParser.parseString(
                syncCommands.get("minecraftuser:" + player.toString())
        ).getAsJsonObject();
    }

    public boolean set(UUID player, JsonObject data) {
        JsonObject json = JsonParser.parseString(
                syncCommands.get("minecraftuser:" + player.toString())
        ).getAsJsonObject();

        return json == null;
    }

}
