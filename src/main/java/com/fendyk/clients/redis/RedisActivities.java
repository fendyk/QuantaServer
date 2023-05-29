package com.fendyk.clients.redis;

import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.Main;
import com.fendyk.clients.RedisAPI;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;

import javax.xml.stream.events.DTD;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RedisActivities extends RedisAPI<ActivitiesDTO> {
    public RedisActivities(String redisKey) {
        super(redisKey, ActivitiesDTO.class);
    }
}
