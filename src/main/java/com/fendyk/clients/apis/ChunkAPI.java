package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.updates.UpdateChunkDTO;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.fetch.FetchChunk;
import com.fendyk.clients.redis.RedisChunk;
import com.fendyk.utilities.Vector2;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.Chunk;
import org.jetbrains.annotations.Nullable;

public class ChunkAPI extends ClientAPI<FetchChunk, RedisChunk> {

    public ChunkAPI(API api, FetchChunk fetch, RedisChunk redis) {
        super(api, fetch, redis);
    }

    @Nullable
    public ChunkDTO get(Chunk chunk) {
        Vector2 chunkPos = new Vector2(chunk.getX(), chunk.getZ());
        return redis.get(chunkPos);
    }

    public ChunkDTO create(Chunk chunk, boolean isClaimable) {
        ChunkDTO newChunkDTO = new ChunkDTO();
        newChunkDTO.setClaimable(isClaimable);
        newChunkDTO.setX(chunk.getX());
        newChunkDTO.setZ(chunk.getZ());
        return fetch.create(newChunkDTO);
    }

    public ChunkDTO update(Chunk chunk, UpdateChunkDTO updates) {
        Vector2 vector2 = new Vector2(chunk.getX(), chunk.getZ());
        return fetch.update(vector2, updates);
    }

    public boolean claim(Chunk chunk, String landId) {
        UpdateChunkDTO updateChunkDTO = new UpdateChunkDTO();
        updateChunkDTO.setLandId(landId);
        return update(chunk,updateChunkDTO) != null;
    }


}