package com.fendyk.clients.redis;

import com.fendyk.DTOs.TeleportDTO;
import com.fendyk.clients.RedisAPI;

public class RedisTeleport extends RedisAPI<TeleportDTO> {
    public RedisTeleport(String redisKey) {
        super(redisKey, TeleportDTO.class);
    }
}
