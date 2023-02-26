package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.fetch.FetchLand;
import com.fendyk.clients.redis.RedisLand;
import com.fendyk.utilities.Vector2;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.leonhard.storage.Json;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.UUID;

public class LandAPI extends ClientAPI<FetchLand, RedisLand> {

    public LandAPI(API api, FetchLand fetch, RedisLand redis) {
        super(api, fetch, redis);
    }

    /**
     * Creates a new land. Requires an existing chunk
     * @param owner
     * @param name
     * @param chunk
     * @return The new land if created or null if something went wrong.
     * @throws Exception When something goes wrong lol
     */
    public LandDTO create(UUID owner, String name, Chunk chunk) throws Exception {

        /* Get references */
        ChunkAPI chunkAPI = api.getChunkAPI();
        MinecraftUserAPI minecraftUserAPI = api.getMinecraftUserAPI();

        /* Get user */
        MinecraftUserDTO minecraftUserDTO = minecraftUserAPI.get(owner, false);
        if(minecraftUserDTO == null) throw new Exception("Could not find user when creating land");

        /* Get chunk */
        ChunkDTO chunkDTO = chunkAPI.get(chunk);

        /* If we cannot find one, create new */
        if(chunkDTO == null) {
            Bukkit.getLogger().info("I need a chunk!");
            chunkDTO = chunkAPI.create(chunk, false);
            if(chunkDTO == null) throw new Exception("Could not create chunk when creating land");
        }

        /* Create the actual land */
        LandDTO landDTO = new LandDTO();
        landDTO.setName(name);
        landDTO.setOwnerId(owner.toString());

        landDTO = fetch.create(landDTO);
        if(landDTO == null) throw new Exception("Could not create land");
        return landDTO;
    }

}