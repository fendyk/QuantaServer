package com.fendyk.clients.redis;

import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.Main;
import com.fendyk.clients.RedisAPI;
import com.fendyk.utilities.Vector2;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RedisChunk extends RedisAPI<Vector2, ChunkDTO> {
    public RedisChunk(Main server,
                      RedisClient client,
                      boolean inDebugMode,
                      ArrayList<RedisPubSubListener<String, String>> listeners,
                      HashMap<String, String> subscriptions) {
        super(server, client, inDebugMode, listeners, subscriptions);
    }

    @Override
    public ChunkDTO get(Vector2 key) {
        return Main.gson.fromJson(
                getCache("chunk:" + key.getX() + ":" + key.getY()),
                ChunkDTO.class
        );
    }

    @Override
    public boolean set(Vector2 key, ChunkDTO data) {
        return setCache("chunk:" + key.getX() + ":" + key.getY(), Main.gson.toJson(data));
    }

    @Override
    public boolean exists(Vector2 key) {
        return false;
    }
}
