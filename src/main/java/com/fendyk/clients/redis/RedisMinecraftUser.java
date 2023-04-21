package com.fendyk.clients.redis;

import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.Main;
import com.fendyk.clients.RedisAPI;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RedisMinecraftUser extends RedisAPI<MinecraftUserDTO> {
    public RedisMinecraftUser() {
        super("minecraftuser:", MinecraftUserDTO.class);
    }

}
