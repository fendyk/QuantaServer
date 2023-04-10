package com.fendyk.clients.redis;

import com.fendyk.DTOs.LandDTO;
import com.fendyk.Main;
import com.fendyk.clients.RedisAPI;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RedisLand extends RedisAPI<String, LandDTO> {
    public RedisLand(Main server,
                              RedisClient client,
                              boolean inDebugMode,
                              ArrayList<RedisPubSubListener<String, String>> listeners,
                              ArrayList<String> subscriptions) {
        super(server, client, inDebugMode, listeners, subscriptions);
    }

    public LandDTO get(String key) {
        return Main.gson.fromJson(
                getCache("land:" + key),
                LandDTO.class
        );
    }

    public LandDTO getMin(String key) {
        return Main.gson.fromJson(
                getCache("land:" + key + ":min"),
                LandDTO.class
        );
    }

    @Override
    public boolean set(String key, LandDTO data) {
        return setCache("land:" + key, Main.gson.toJson(data));
    }

    @Override
    public boolean exists(String key) {
        return existsInCache("land:" + key);
    }
}
