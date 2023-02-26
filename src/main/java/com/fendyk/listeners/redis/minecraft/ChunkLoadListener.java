package com.fendyk.listeners.redis.minecraft;

import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.Main;
import com.fendyk.utilities.Vector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.RegionResultSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class ChunkLoadListener implements Listener {

    HashMap<String, Chunk> checkedChunks;
    Main server;

    String worldName;

    public ChunkLoadListener(Main server) {
        this.server = server;
        this.checkedChunks = new HashMap<>();

        this.worldName = server.getTomlConfig().getString("worldName");
    }

    @EventHandler
    public void onChunkUnload(PlayerChunkUnloadEvent event) {
        if(!event.getWorld().getName().equalsIgnoreCase(worldName)) return;
        Chunk chunk = event.getChunk();
        String key = chunk.getX() + ":" + chunk.getZ();
        if(!checkedChunks.containsKey(key)) return;

        Bukkit.getLogger().info(chunk.getX() + "/" + chunk.getZ() + " unloaded");

        checkedChunks.remove(key);
    }

    /**
     * We need to verify if chunks are up-to-date.
     * If it does not exist, we can simply return.
     * We keep track of the chunks that are verified
     * @param event
     */
    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent event) throws StorageException {
        if(!event.getWorld().getName().equalsIgnoreCase(worldName)) return;
        Chunk chunk = event.getChunk();
        final String key = chunk.getX() + ":" + chunk.getZ();
        if(checkedChunks.containsKey(key)) return;

        Bukkit.getLogger().info(chunk.getX() + "/" + chunk.getZ() + " loaded");

        /* To avoid unnecessary calls to the api, first check if we already CACHED the chunk  */
        boolean isCached = server.getApi().getChunkAPI().getRedis().exists(new Vector2(chunk.getX(), chunk.getZ()));

        if(!isCached) {
            Bukkit.getLogger().info(chunk.getX() + "/" + chunk.getZ() + " needs an verification");
            ChunkDTO chunkDTO = server.getApi().getChunkAPI().get(chunk);
            if(chunkDTO == null) return; // Could not find so no need for check

            /* Find the land by landID */
            LandDTO land = server.getApi().getLandAPI().getRedis().getById(chunkDTO.getLandId());
            if(land == null) return;


            /* Find the region */
            Location center = getChunkCenter(chunk);
            Set<ProtectedRegion> set = server.getRegionManager().getApplicableRegions(
                    BlockVector3.at(center.x(), center.y(), center.z())
            ).getRegions();

            /* If we cannot find the region but the land has a landOwnerId, we need to sync */
            if(set.size() < 1) {
                Location topLeft = chunk.getBlock(0,-64,0).getLocation();
                Location bottomRight = chunk.getBlock(15,320,15).getLocation();

                BlockVector3 min = BlockVector3.at(topLeft.getX(), -256, topLeft.getZ());
                BlockVector3 max = BlockVector3.at(bottomRight.getX(), 256, bottomRight.getZ());
                ProtectedRegion newRegion = new ProtectedCuboidRegion(key, min, max);

                setRegionMembersAndOwner(newRegion, land.getOwnerId(), land.getMemberIDs()); // Updates the region
                server.getRegionManager().addRegion(newRegion); // Dont forget to save the region
            }

            /* Verify if the current region owner is equal to the chunk */
            for (ProtectedRegion region : set) {
                if(region.getId().equalsIgnoreCase(key)) { // If we found the region with the correct key

                    setRegionMembersAndOwner(region, land.getOwnerId(), land.getMemberIDs()); // Updates the region
                    server.getRegionManager().save(); // Dont forget to save the region
                }
            }
        }

        checkedChunks.put(key, chunk); // We've checked this region so no need for a re-check
    }

    public void setRegionMembersAndOwner(ProtectedRegion region, String landOwnerId, ArrayList<String> memberIds) {
        DefaultDomain newOwners = new DefaultDomain();
        DefaultDomain newMembers = new DefaultDomain();

        UUID landOwnerUuid = UUID.fromString(landOwnerId);

        for(String memberId : memberIds) {
            newMembers.addPlayer(UUID.fromString(memberId));
        }
        newOwners.addPlayer(landOwnerUuid);

        /* Update the region owners and members */
        region.setOwners(newOwners);
        region.setMembers(newMembers);
    }

    public Location getChunkCenter(Chunk chunk) {
        int x = chunk.getX() << 4 + 8; // calculate the X coordinate of the center of the chunk
        int z = chunk.getZ() << 4 + 8; // calculate the Z coordinate of the center of the chunk
        int y = chunk.getWorld().getHighestBlockYAt(x, z); // get the highest block Y coordinate at the center of the chunk
        return new Location(chunk.getWorld(), x, y, z); // create and return a new Location object with the center coordinates
    }
}
