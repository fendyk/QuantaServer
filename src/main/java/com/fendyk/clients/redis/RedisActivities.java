package com.fendyk.clients.redis;

import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.Main;
import com.fendyk.clients.RedisAPI;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.util.ArrayList;
import java.util.UUID;

public class RedisActivities extends RedisAPI<UUID, ActivitiesDTO> {
    public RedisActivities(Main server,
                     RedisClient client,
                     boolean inDebugMode,
                     ArrayList<RedisPubSubListener<String, String>> listeners,
                     ArrayList<String> subscriptions) {
        super(server, client, inDebugMode, listeners, subscriptions);
    }

    public ActivitiesDTO get(UUID player) {
        return Main.gson.fromJson(
                getCache("activities:" + player.toString()),
                ActivitiesDTO.class
        );
    }

    @Override
    public boolean set(UUID player, ActivitiesDTO data) {
        return false;
    }

    @Override
    public boolean exists(UUID player) {
        return existsInCache("activities:" + player.toString());
    }
}
