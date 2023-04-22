package com.fendyk.clients.redis

import com.fendyk.DTOs.MinecraftUserDTO
import com.fendyk.clients.RedisAPI
import java.util.UUID
import java.util.concurrent.CompletableFuture

class RedisMinecraftUser : RedisAPI<UUID, MinecraftUserDTO>("minecraftuser:", MinecraftUserDTO::class.java) {
    override fun get(key: UUID): CompletableFuture<MinecraftUserDTO> {
        return fetch(key.toString())
    }
    override fun set(key: UUID, dto: MinecraftUserDTO): CompletableFuture<Boolean> {
        return save(key.toString(), dto)
    }
}
