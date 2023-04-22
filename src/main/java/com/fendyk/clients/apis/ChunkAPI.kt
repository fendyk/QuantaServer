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

    operator fun get(chunk: Chunk): CompletableFuture<ChunkDTO> {
        val chunkPos = Vector2(chunk.x, chunk.z)
        return redis!!.get(chunkPos)
    }

    fun create(chunk: Chunk, isClaimable: Boolean): CompletableFuture<ChunkDTO> {
        return CompletableFuture.supplyAsync {
            val newChunkDTO = ChunkDTO(chunk.x, chunk.z)
            newChunkDTO.isClaimable = isClaimable
            fetch.create(newChunkDTO)
        }
    }

    fun update(chunk: Chunk, updates: UpdateChunkDTO): CompletableFuture<ChunkDTO> {
        val vector2 = Vector2(chunk.x, chunk.z)
        return fetch!!.update(vector2, updates)
    }

    fun claim(chunk: Chunk, landId: String, canExpire: Boolean, expirationDate: DateTime): Boolean {
        val updateChunkDTO = UpdateChunkDTO()
        updateChunkDTO.landId = landId
        updateChunkDTO.canExpire = canExpire
        updateChunkDTO.expirationDate = expirationDate
        return update(chunk, updateChunkDTO) != null
    }

    fun expire(chunk: Chunk): Boolean {
        val updateChunkDTO = UpdateChunkDTO()
        updateChunkDTO.resetLandId = true
        updateChunkDTO.canExpire = false
        updateChunkDTO.resetExpirationDate = true
        return update(chunk, updateChunkDTO) != null
    }

    fun extend(chunk: Chunk, days: Int): Boolean {
        val chunkDTO = get(chunk) ?: return false
        val expirationDate = chunkDTO.getExpirationDate() ?: return false
        val newExpirationDate = expirationDate.plusDays(days)
        val updateChunkDTO = UpdateChunkDTO()
        updateChunkDTO.canExpire = true
        updateChunkDTO.expirationDate = newExpirationDate
        return update(chunk, updateChunkDTO) != null
    }

    companion object {
        @JvmStatic
        fun isBlacklistedBlock(chunkDTO: ChunkDTO, block: Block): Boolean {
            return chunkDTO.blacklistedBlocks.stream().anyMatch { (x, y, z): BlacklistedBlockDTO -> x == block.x && y == block.y && z == block.z }
        }

        /**
         * Returns true if chunk is claimable
         * @param chunkDTO
         * @return
         */
        @JvmStatic
        fun isClaimable(chunkDTO: ChunkDTO): Boolean {
            return chunkDTO.isClaimable
        }
    }
}