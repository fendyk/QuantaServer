package com.fendyk.clients.redis;

import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.Main;
import com.fendyk.clients.RedisAPI;
import com.fendyk.utilities.Vector2;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RedisChunk extends RedisAPI<ChunkDTO> {
    public RedisChunk(String key) {
        super(key, ChunkDTO.class);
    }
}
