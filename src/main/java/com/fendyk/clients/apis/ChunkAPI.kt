package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.DTOs.BlacklistedBlockDTO;
import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.DTOs.updates.UpdateChunkDTO;
import com.fendyk.Main;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.fetch.FetchChunk;
import com.fendyk.clients.redis.RedisChunk;
import com.fendyk.utilities.Vector2;
import net.luckperms.api.model.user.User;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.UUID;

public class ChunkAPI extends ClientAPI<FetchChunk, RedisChunk, String, ChunkDTO> {

    public ChunkAPI(API api, FetchChunk fetch, RedisChunk redis) {
        super(api, fetch, redis);
    }

    public static enum ChunkState {
        BLACKLISTED,
        UNCLAIMABLE,
        UNCLAIMED,
        CLAIMED_EXPIRABLE,
        CLAIMED_PERMANENT,
    }


    @Nullable
    public ChunkDTO get(Chunk chunk) {
        Vector2 chunkPos = new Vector2(chunk.getX(), chunk.getZ());
        return redis.get(chunkPos);
    }

    public ChunkDTO create(Chunk chunk, boolean isClaimable) {
        ChunkDTO newChunkDTO = new ChunkDTO();
        newChunkDTO.setClaimable(isClaimable);
        newChunkDTO.x = chunk.getX();
        newChunkDTO.z = chunk.getZ();
        return fetch.create(newChunkDTO);
    }

    public ChunkDTO update(Chunk chunk, UpdateChunkDTO updates) {
        Vector2 vector2 = new Vector2(chunk.getX(), chunk.getZ());
        return fetch.update(vector2, updates);
    }

    public boolean claim(Chunk chunk, String landId, boolean canExpire, DateTime expirationDate) {
        UpdateChunkDTO updateChunkDTO = new UpdateChunkDTO();
        updateChunkDTO.landId = landId;
        updateChunkDTO.setCanExpire(canExpire);
        updateChunkDTO.setExpirationDate(expirationDate);

        return update(chunk, updateChunkDTO) != null;
    }

    public boolean expire(Chunk chunk) {
        UpdateChunkDTO updateChunkDTO = new UpdateChunkDTO();
        updateChunkDTO.resetLandId = true;
        updateChunkDTO.setCanExpire(false);
        updateChunkDTO.resetExpirationDate = true;

        return update(chunk, updateChunkDTO) != null;
    }

    public boolean extend(Chunk chunk, int days) {
        ChunkDTO chunkDTO = get(chunk);
        if(chunkDTO == null) return false;

        DateTime expirationDate = chunkDTO.getExpirationDate();
        if(expirationDate == null) return false;

        DateTime newExpirationDate = expirationDate.plusDays(days);

        UpdateChunkDTO updateChunkDTO = new UpdateChunkDTO();
        updateChunkDTO.setCanExpire(true);
        updateChunkDTO.setExpirationDate(newExpirationDate);
        return update(chunk, updateChunkDTO) != null;
    }


    public static boolean isBlacklistedBlock(ChunkDTO chunkDTO, Block block) {
        return chunkDTO.blacklistedBlocks.stream().anyMatch(item ->
            item.x == block.getX() &&
                    item.y == block.getY() &&
                    item.z == block.getZ()
        );
    }

    /**
     * Returns true if chunk is claimable
     * @param chunkDTO
     * @return
     */
    public static boolean isClaimable(ChunkDTO chunkDTO) {
        return chunkDTO.isClaimable;
    }

}