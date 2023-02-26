package com.fendyk.clients.redis;

import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.Main;
import com.fendyk.clients.RedisAPI;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RedisMinecraftUser extends RedisAPI<UUID, MinecraftUserDTO> {

    public RedisMinecraftUser(Main server,
                     RedisClient client,
                     boolean inDebugMode,
                     HashMap<String, RedisPubSubListener<String, String>> subscriptions) {
        super(server, client, inDebugMode, subscriptions);
    }

    public MinecraftUserDTO get(UUID player) {
        return Main.gson.fromJson(
                getCache("minecraftuser:" + player.toString()),
                MinecraftUserDTO.class
        );
    }

    public boolean set(UUID player, MinecraftUserDTO data) {
        return setCache("minecraftuser:" + player.toString(), Main.gson.toJson(data));
    }

    @Override
    public boolean exists(UUID key) {
        return false;
    }

}
