package com.fendyk.clients.apis

import com.fendyk.API
import com.fendyk.DTOs.BlacklistedBlockDTO
import com.fendyk.DTOs.ChunkDTO
import com.fendyk.DTOs.updates.UpdateChunkDTO
import com.fendyk.clients.ClientAPI
import com.fendyk.clients.fetch.FetchChunk
import com.fendyk.clients.redis.RedisChunk
import com.fendyk.utilities.Vector2
import org.bukkit.Chunk
import org.bukkit.block.Block
import org.joda.time.DateTime
import java.util.concurrent.CompletableFuture

class ChunkAPI(fetch: FetchChunk, redis: RedisChunk) : ClientAPI<FetchChunk, RedisChunk, String, ChunkDTO?>(fetch, redis) {
    enum class ChunkState {
        BLACKLISTED,
        UNCLAIMABLE,
        UNCLAIMED,
        CLAIMED_EXPIRABLE,
        CLAIMED_PERMANENT
    }

    fun get(chunk: Chunk): CompletableFuture<ChunkDTO?> {
        return CompletableFuture.supplyAsync {
            val chunkPos = Vector2(chunk.x, chunk.z)
            return@supplyAsync redis.get(chunkPos).get()
        }
    }

    fun create(chunk: Chunk, isClaimable: Boolean): CompletableFuture<ChunkDTO?> {
        return CompletableFuture.supplyAsync {
            val newChunkDTO = ChunkDTO(chunk.x, chunk.z)
            newChunkDTO.isClaimable = isClaimable
            return@supplyAsync fetch.create(newChunkDTO).get()
        }
    }

    fun update(chunk: Chunk, updates: UpdateChunkDTO): CompletableFuture<ChunkDTO?> {
        return CompletableFuture.supplyAsync {
            val vector2 = Vector2(chunk.x, chunk.z)
            return@supplyAsync fetch.update(vector2, updates).get()
        }
    }

    fun claim(chunk: Chunk, landId: String, canExpire: Boolean, expirationDate: DateTime?): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            val updateChunkDTO = UpdateChunkDTO()
            updateChunkDTO.landId = landId
            updateChunkDTO.canExpire = canExpire
            updateChunkDTO.expirationDate = expirationDate.toString()
            return@supplyAsync update(chunk, updateChunkDTO).isDone
        }
    }

    fun expire(chunk: Chunk): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            val updateChunkDTO = UpdateChunkDTO()
            updateChunkDTO.resetLandId = true
            updateChunkDTO.canExpire = false
            updateChunkDTO.resetExpirationDate = true
            return@supplyAsync update(chunk, updateChunkDTO).isDone
        }
    }

    fun extend(chunk: Chunk, days: Int):  CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            val chunkDTO: ChunkDTO = get(chunk).get() ?: throw Exception("Could not find the chunk.")
            val expirationDate = chunkDTO.getExpirationDate()
            val newExpirationDate = expirationDate.plusDays(days)
            val updateChunkDTO = UpdateChunkDTO()
            updateChunkDTO.canExpire = true
            updateChunkDTO.expirationDate = newExpirationDate.toString()
            return@supplyAsync update(chunk, updateChunkDTO).isDone
        }
    }

    companion object {
        @JvmStatic
        fun isBlacklistedBlock(chunkDTO: ChunkDTO, block: Block): Boolean {
            return chunkDTO.blacklistedBlocks.stream().anyMatch { (x, y, z): BlacklistedBlockDTO -> x == block.x && y == block.y && z == block.z }
        }
    }
}