package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.DTOs.updates.UpdateChunkDTO;
import com.fendyk.clients.ApiClient;
import com.fendyk.clients.fetch.FetchChunk;
import com.fendyk.clients.redis.RedisChunk;
import com.fendyk.utilities.Vector2;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.netty.util.internal.ObjectUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.Chunk;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ChunkAPI extends ApiClient {

    API api;
    FetchChunk fetch;
    RedisChunk redis;

    public ChunkAPI(API api, FetchChunk fetch, RedisChunk redis) {
        this.api = api;
        this.fetch = fetch;
        this.redis = redis;
    }

    @Nullable
    public ChunkDTO get(Chunk chunk) {
        Vector2 chunkPos = new Vector2(chunk.getX(), chunk.getZ());
        return ObjectUtils.firstNonNull(redis.get(chunkPos), fetch.get(chunkPos));
    }

    public ChunkDTO create(Chunk chunk, boolean isClaimable) {
        ChunkDTO newChunkDTO = new ChunkDTO();
        newChunkDTO.setClaimable(isClaimable);
        newChunkDTO.setX(chunk.getX());
        newChunkDTO.setZ(chunk.getZ());

        newChunkDTO = fetch.create(newChunkDTO);

        if(newChunkDTO == null) return null;

        boolean isCached = redis.set(new Vector2(chunk.getX(), chunk.getZ()), newChunkDTO);
        return isCached ? newChunkDTO : null;
    }

    public ChunkDTO update(Chunk chunk, UpdateChunkDTO updates) {
        Vector2 vector2 = new Vector2(chunk.getX(), chunk.getZ());
        ChunkDTO updatedChunk = fetch.update(vector2, updates);
        if(updatedChunk == null) return null;
        boolean isCached = redis.set(vector2, updatedChunk);
        return isCached ? updatedChunk : null;
    }

    public boolean claim(Chunk chunk, String landId) {
        UpdateChunkDTO updateChunkDTO = new UpdateChunkDTO();
        updateChunkDTO.setLandId(landId);
        ChunkDTO chunkDTO = update(chunk,updateChunkDTO );
        return chunkDTO != null;
    }


}