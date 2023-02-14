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

public class RedisMinecraftUser extends RedisAPI<UUID> {

    public RedisMinecraftUser(QuantaServer server,
                     RedisClient client,
                     boolean inDebugMode,
                     ArrayList<RedisPubSubListener<String, String>> listeners,
                     HashMap<String, String> subscriptions) {
        super(server, client, inDebugMode, listeners, subscriptions);
    }

    public JsonElement get(UUID player) {
        return getCache("minecraftuser:" + player.toString());
    }

    public boolean set(UUID player, JsonObject data) {
        return setCache("minecraftuser:" + player.toString(), data);
    }

}
