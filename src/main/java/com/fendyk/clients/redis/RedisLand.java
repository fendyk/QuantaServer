package com.fendyk.clients.redis;

import com.fendyk.DTOs.LandDTO;
import com.fendyk.QuantaServer;
import com.fendyk.clients.RedisAPI;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RedisLand extends RedisAPI<UUID, LandDTO> {
    public RedisLand(QuantaServer server,
                      RedisClient client,
                      boolean inDebugMode,
                      ArrayList<RedisPubSubListener<String, String>> listeners,
                      HashMap<String, String> subscriptions) {
        super(server, client, inDebugMode, listeners, subscriptions);
    }

    @Override
    public LandDTO get(UUID key) {
        return QuantaServer.gson.fromJson(
                getCache("land:" + key.toString()),
                LandDTO.class
        );
    }

    @Override
    public boolean set(UUID key, LandDTO data) {
        return setCache("land:" + key.toString(), QuantaServer.gson.toJson(data));
    }
}
