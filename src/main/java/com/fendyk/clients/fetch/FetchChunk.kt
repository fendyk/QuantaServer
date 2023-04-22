package com.fendyk.clients.fetch

import com.fendyk.DTOs.ChunkDTO
import com.fendyk.DTOs.MinecraftUserDTO
import com.fendyk.DTOs.updates.UpdateChunkDTO
import com.fendyk.DTOs.updates.UpdateMinecraftUserDTO
import com.fendyk.Main
import com.fendyk.clients.FetchAPI
import com.fendyk.utilities.Vector2
import okhttp3.Request
import okhttp3.RequestBody
import java.util.*
import java.util.concurrent.CompletableFuture

class FetchChunk : FetchAPI<Vector2, ChunkDTO, UpdateChunkDTO>(ChunkDTO::class.java) {

    override fun get(key: Vector2): CompletableFuture<ChunkDTO> {
        return fetch("/chunks/${key.x}/${key.y}", RequestMethod.GET, null)
    }

    override fun create(dto: ChunkDTO): CompletableFuture<ChunkDTO> {
        return fetch("/chunks", RequestMethod.PATCH, dto)
    }

    override fun update(key: Vector2, dto: UpdateChunkDTO): CompletableFuture<ChunkDTO> {
        return fetch("/chunks/${key.x}/${key.y}", RequestMethod.PATCH, dto)
    }
}
