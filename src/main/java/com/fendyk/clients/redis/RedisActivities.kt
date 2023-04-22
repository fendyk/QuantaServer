package com.fendyk.clients.redis

import com.fendyk.DTOs.ActivitiesDTO
import com.fendyk.clients.RedisAPI
import java.util.UUID
import java.util.concurrent.CompletableFuture

class RedisActivities : RedisAPI<UUID, ActivitiesDTO>("activities:", ActivitiesDTO::class.java) {
    override fun get(key: UUID): CompletableFuture<ActivitiesDTO> {
        return fetch(key.toString())
    }

    override fun set(key: UUID, dto: ActivitiesDTO): CompletableFuture<Boolean> {
        return save(key.toString(), dto)
    }
}
