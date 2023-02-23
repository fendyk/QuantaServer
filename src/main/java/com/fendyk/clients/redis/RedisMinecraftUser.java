package com.fendyk.clients.redis;

import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.QuantaServer;
import com.fendyk.clients.RedisAPI;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RedisMinecraftUser extends RedisAPI<UUID, MinecraftUserDTO> {

    public RedisMinecraftUser(QuantaServer server,
                              RedisClient client,
                              boolean inDebugMode,
                              ArrayList<RedisPubSubListener<String, String>> listeners,
                              HashMap<String, String> subscriptions) {
        super(server, client, inDebugMode, listeners, subscriptions);
    }

    public MinecraftUserDTO get(UUID player) {
        return QuantaServer.gson.fromJson(
                getCache("minecraftuser:" + player.toString()),
                MinecraftUserDTO.class
        );
    }

    public boolean set(UUID player, MinecraftUserDTO data) {
        return setCache("minecraftuser:" + player.toString(), QuantaServer.gson.toJson(data));
    }

}
