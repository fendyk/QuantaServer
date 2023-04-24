package com.fendyk.clients.redis

import com.fendyk.DTOs.ChunkDTO
import com.fendyk.clients.RedisAPI
import com.fendyk.utilities.Vector2
import java.util.concurrent.CompletableFuture

class RedisChunk : RedisAPI<Vector2, ChunkDTO>("chunk:", ChunkDTO::class.java) {
    override fun get(key: Vector2): CompletableFuture<ChunkDTO?> {
        return fetch("${key.x}:${key.y}")
    }

    override fun set(key: Vector2, dto: ChunkDTO): CompletableFuture<Boolean> {
        return save("${key.x}:${key.y}", dto)
    }
}
