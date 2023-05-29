package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.DTOs.TaggedLocationDTO;
import com.fendyk.Main;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.fetch.FetchLand;
import com.fendyk.clients.redis.RedisLand;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LandAPI extends ClientAPI<FetchLand, RedisLand, String, LandDTO> {

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
    public LandDTO create(Player owner, String name, Chunk chunk, Location location) throws Exception {
        UUID uuid = owner.getUniqueId();
        User user = Main.getInstance().getLuckPermsApi().getUserManager().getUser(uuid);

        if(user == null) return null;

        /* Get references */
        ChunkAPI chunkAPI = api.getChunkAPI();
        MinecraftUserAPI minecraftUserAPI = api.getMinecraftUserAPI();

        /* Get user */
        MinecraftUserDTO minecraftUserDTO = minecraftUserAPI.get(uuid);
        if(minecraftUserDTO == null) throw new Exception("Could not find user when creating land");

        /* Get chunk */
        ChunkDTO chunkDTO = chunkAPI.get(chunk);

        /* If we cannot find one, create new */
        if(chunkDTO == null) {
            Bukkit.getLogger().info("I need a chunk!");
            chunkDTO = chunkAPI.create(chunk, false);
            if(chunkDTO == null) throw new Exception("Could not create chunk when creating land");
        }

        TaggedLocationDTO taggedLocationDTO = new TaggedLocationDTO("spawn", location);

        /* Create the actual land */
        LandDTO landDTO = new LandDTO();
        landDTO.setName(name);
        landDTO.setOwnerId(owner.getUniqueId().toString());
        landDTO.getHomes().add(taggedLocationDTO);

        CompletableFuture<LandDTO> aFuture = fetch.create(landDTO);
        landDTO = aFuture.get();

        if(landDTO == null) throw new Exception("Could not create land");

        DateTime expireDate = new DateTime();
        expireDate.plusMinutes(2);

        String primaryGroup = user.getPrimaryGroup();
        boolean canExpire = primaryGroup.equalsIgnoreCase("default") || primaryGroup.equalsIgnoreCase("barbarian");

        /* Claim the chunk */
        chunkAPI.claim(chunk, landDTO.getId(), canExpire, canExpire ? expireDate : null);

        cachedRecords.put(landDTO.getId(), landDTO);

        return landDTO;
    }

    /**
     * Gets the land via land's id
     * @param id
     * @return LandDTO or null if not found
     */
    public LandDTO get(String id) throws ExecutionException, InterruptedException {
        CompletableFuture<LandDTO> dto = redis.get(id);
        cachedRecords.put(id, dto.get());
        return dto.get();
    }

    /**
     * Gets the land via player's uuid
     * @param player
     * @return LandDTO or null if not found
     */
    public LandDTO get(UUID player) throws ExecutionException, InterruptedException {
        CompletableFuture<LandDTO> dto = redis.get(player.toString());
        cachedRecords.put(player.toString(), dto.get());
        return dto.get();
    }

}