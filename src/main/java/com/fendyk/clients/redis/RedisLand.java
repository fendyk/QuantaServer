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
                      HashMap<String, RedisPubSubListener<String, String>> subscriptions) {
        super(server, client, inDebugMode, subscriptions);
    }
    public LandDTO get(String key) {
        return Main.gson.fromJson(
                getCache("land:" + key),
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
