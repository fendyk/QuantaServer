package com.fendyk.clients.redis

import com.fendyk.DTOs.LandDTO
import com.fendyk.clients.RedisAPI
import java.util.concurrent.CompletableFuture

class RedisLand : RedisAPI<String, LandDTO>("land:", LandDTO::class.java) {
    override fun get(key: String): CompletableFuture<LandDTO> {
        return fetch(key)
    }
}
