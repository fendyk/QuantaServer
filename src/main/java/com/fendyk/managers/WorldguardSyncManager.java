package com.fendyk.managers;

import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.Main;
import com.fendyk.utilities.Vector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public final class WorldguardSyncManager {


    public static Main server;

    public static Location getChunkCenter(Chunk chunk) {
        int x = chunk.getX() << 4 + 8; // calculate the X coordinate of the center of the chunk
        int z = chunk.getZ() << 4 + 8; // calculate the Z coordinate of the center of the chunk
        int y = chunk.getWorld().getHighestBlockYAt(x, z); // get the highest block Y coordinate at the center of the chunk
        return new Location(chunk.getWorld(), x, y, z); // create and return a new Location object with the center coordinates
    }

    public static void setRegionMembersAndOwner(ProtectedRegion region, @Nullable  String landOwnerId, @Nullable  ArrayList<String> memberIds) {
        DefaultDomain newOwners = new DefaultDomain();
        DefaultDomain newMembers = new DefaultDomain();

        if(landOwnerId != null) {
            UUID landOwnerUuid = UUID.fromString(landOwnerId);
            newOwners.addPlayer(landOwnerUuid);
        }

        if(memberIds != null) {
            for(String memberId : memberIds) {
                newMembers.addPlayer(UUID.fromString(memberId));
            }
        }

        /* Update the region owners and members */
        region.setOwners(newOwners);
        region.setMembers(newMembers);
    }

    public static void syncChunkWithRegion(Chunk chunk, @Nullable ChunkDTO chunkDTO) throws StorageException {
        Bukkit.getLogger().info(chunk.getX() + "/" + chunk.getZ() + " is trying to sync with region");

        /* If it's cached, we're going to do stuff with it. */
        if(chunkDTO == null) {
            /* To avoid unnecessary calls to the api, first check if we already CACHED the chunk  */
            boolean isCached = server.getApi().getChunkAPI().getRedis().exists(new Vector2(chunk.getX(), chunk.getZ()));
            if(!isCached) return;
            chunkDTO = server.getApi().getChunkAPI().get(chunk);
            if(chunkDTO == null) return; // Could not find so no need for check
        }

        /* Find the land by landID */
        LandDTO landDTO = server.getApi().getLandAPI().getRedis().get(chunkDTO.getLandId());
        if(landDTO == null) return;

        /* Find the region */
        Location center = WorldguardSyncManager.getChunkCenter(chunk);
        Set<ProtectedRegion> set = server.getRegionManager().getApplicableRegions(
                BlockVector3.at(center.x(), center.y(), center.z())
        ).getRegions();

        /* If we cannot find the region but the land has a landOwnerId, we need to sync */
        if(set.size() < 1) {
            Location topLeft = chunk.getBlock(0,-64,0).getLocation();
            Location bottomRight = chunk.getBlock(15,320,15).getLocation();

            BlockVector3 min = BlockVector3.at(topLeft.getX(), -256, topLeft.getZ());
            BlockVector3 max = BlockVector3.at(bottomRight.getX(), 256, bottomRight.getZ());
            ProtectedRegion newRegion = new ProtectedCuboidRegion(chunkDTO.getId(), min, max);

            WorldguardSyncManager.setRegionMembersAndOwner(newRegion,
                    landDTO.getOwnerId(),
                    landDTO.getMemberIDs()
            ); // Updates the region
            server.getRegionManager().addRegion(newRegion); // Dont forget to save the region
        }

        /* Verify if the current region owner is equal to the chunk */
        for (ProtectedRegion regionChild : set) {
            if(regionChild.getId().equalsIgnoreCase(chunkDTO.getId())) { // If we found the region with the correct key

                WorldguardSyncManager.setRegionMembersAndOwner(regionChild,
                        landDTO.getOwnerId(),
                        landDTO.getMemberIDs()
                ); // Updates the region
            }
        }

        server.getRegionManager().save(); // Dont forget to save the region
    }

}
