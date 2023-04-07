package com.fendyk.managers;

import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.Main;
import com.fendyk.clients.apis.ChunkAPI;
import com.fendyk.utilities.ChunkUtils;
import com.fendyk.utilities.Log;
import com.fendyk.utilities.Vector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.*;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.ParticleData;
import xyz.xenondevs.particle.data.color.DustData;
import xyz.xenondevs.particle.task.TaskManager;

import java.util.*;

public final class WorldguardSyncManager {

    static Main main = Main.getInstance();

    public static List<Chunk> getNeighboringChunks(Chunk chunk) {
        List<Chunk> chunks = new ArrayList<>();
        World world = chunk.getWorld();

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        // Get the neighboring chunks
        chunks.add(world.getChunkAt(chunkX + 1, chunkZ));
        chunks.add(world.getChunkAt(chunkX - 1, chunkZ));
        chunks.add(world.getChunkAt(chunkX, chunkZ + 1));
        chunks.add(world.getChunkAt(chunkX, chunkZ - 1));
        return chunks;
    }

    public static void initialize(int radius, int minYHeight, int maxYHeight) throws StorageException {
        Location[] areaCorners = ChunkUtils.getAreaCorners(radius);
        BlockVector3 topLeft = BlockVector3.at(areaCorners[0].getX(), minYHeight, areaCorners[0].getZ());
        BlockVector3 topBottomRight = BlockVector3.at(areaCorners[3].getX(), maxYHeight, areaCorners[3].getZ());

        RegionManager overworldRegionManager = main.getOverworldRegionManager();

        Set<ProtectedRegion> set = overworldRegionManager.getApplicableRegions(
                BlockVector3.at(0, 0, 0)
        ).getRegions();
        Optional<ProtectedRegion> optionalRegion = set.stream().filter(r -> !r.getId().equalsIgnoreCase("spawn")).findFirst();
        ProtectedRegion region;

        // Remove region if present since we cannot update bounds with Worldguard API
        optionalRegion.ifPresent(protectedRegion -> overworldRegionManager.removeRegion(protectedRegion.getId()));

        region = new ProtectedCuboidRegion("spawn", topLeft, topBottomRight);
        region.setFlag(Flags.BUILD, StateFlag.State.DENY);
        region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
        region.setFlag(Flags.PVP, StateFlag.State.DENY);
        region.setFlag(Flags.LEAF_DECAY, StateFlag.State.DENY);
        region.setFlag(Flags.FIRE_SPREAD, StateFlag.State.DENY);
        region.setFlag(Flags.FALL_DAMAGE, StateFlag.State.DENY);
        region.setFlag(Flags.HUNGER_DRAIN, StateFlag.State.DENY);
        region.setFlag(Flags.FALL_DAMAGE, StateFlag.State.DENY);
        region.setFlag(Flags.TNT, StateFlag.State.DENY);

        GlobalProtectedRegion globalProtectedRegion = new GlobalProtectedRegion("__global__");
        globalProtectedRegion.setFlag(Flags.BUILD, StateFlag.State.ALLOW);
        globalProtectedRegion.setFlag(Flags.BLOCK_PLACE, StateFlag.State.ALLOW);
        globalProtectedRegion.setFlag(Flags.BLOCK_BREAK, StateFlag.State.ALLOW);
        globalProtectedRegion.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);
        globalProtectedRegion.setFlag(Main.BARBARIAN_BUILD, StateFlag.State.DENY); // Set the build flag to deny for the Member rank
        globalProtectedRegion.setFlag(Flags.TNT, StateFlag.State.DENY);

        overworldRegionManager.addRegion(globalProtectedRegion);
        overworldRegionManager.addRegion(region);
        overworldRegionManager.save();
    }

    public static List<Location> getChunkBounds(Chunk chunk, double height) {
        List<Location> bounds = new ArrayList<Location>();
        int minX = chunk.getX() * 16;
        int minZ = chunk.getZ() * 16;
        int maxX = minX + 15;
        int maxZ = minZ + 15;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Location location = new Location(chunk.getWorld(), x, height, z);
                // found a block at this location, check if it's at the edge of the chunk
                if (x == minX || x == maxX || z == minZ || z == maxZ) {
                    bounds.add(location);
                }
            }
        }
        return bounds;
    }

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

    public static void syncChunkWithRegion(Chunk chunk, @Nullable ChunkDTO chunkDTO, @Nullable LandDTO landDTO) throws StorageException {
        Log.info(chunk.getX() + "/" + chunk.getZ() + " is trying to sync with region");

        /* If it's cached, we're going to do stuff with it. */
        if(chunkDTO == null) {
            /* To avoid unnecessary calls to the api, first check if we already CACHED the chunk  */
            boolean isCached = main.getApi().getChunkAPI().getRedis().exists(new Vector2(chunk.getX(), chunk.getZ()));
            if(!isCached) return;
            chunkDTO = main.getApi().getChunkAPI().get(chunk);
            if(chunkDTO == null) return; // Could not find so no need for check
        }

        /* Find the land by landID */
        if(landDTO == null) {
            landDTO = main.getApi().getLandAPI().getRedis().get(chunkDTO.getLandId());
            if(landDTO == null) return;
        }

        /* Find the region */
        Location center = WorldguardSyncManager.getChunkCenter(chunk);
        Set<ProtectedRegion> set = main.getOverworldRegionManager().getApplicableRegions(
                BlockVector3.at(center.x(), center.y(), center.z())
        ).getRegions();

        ProtectedRegion region;

        DateTime expireDate = chunkDTO.getExpirationDate();
        boolean hasExpired = expireDate != null && expireDate.isBeforeNow();

        // If the chunk can expire, keep track of it
        if(chunkDTO.canExpire() && expireDate != null) {
            Log.info("We've detected a expirable chunk : " + expireDate);
            ChunkManager.getExpirableChunks().put(expireDate, chunk);
        }

        /* If we cannot find the region but the land has a landOwnerId, we need to sync */
        if(set.size() < 1) {
            Location topLeft = chunk.getBlock(0,-64,0).getLocation();
            Location bottomRight = chunk.getBlock(15,320,15).getLocation();

            BlockVector3 min = BlockVector3.at(topLeft.getX(), -256, topLeft.getZ());
            BlockVector3 max = BlockVector3.at(bottomRight.getX(), 256, bottomRight.getZ());
            region = new ProtectedCuboidRegion(chunkDTO.getId(), min, max);

            if(!hasExpired) {
                WorldguardSyncManager.setRegionMembersAndOwner(region,
                        landDTO.getOwnerId(),
                        landDTO.getMemberIDs()
                );
            }
            main.getOverworldRegionManager().addRegion(region); // Dont forget to save the region
        }
        else {
            @Nullable ChunkDTO finalChunkDTO = chunkDTO;

            List<ProtectedRegion> regions = set.stream().filter(r -> !r.getId().equalsIgnoreCase(finalChunkDTO.getId())).toList();

            // If we find a region that is not matching our requirements, remove it.
            if(regions.size() > 0) {
                regions.forEach(r -> {
                    main.getOverworldRegionManager().removeRegion(r.getId());
                });
            }

            // Now Find any id matching ours
            Optional<ProtectedRegion> optionalRegion = set.stream().filter(r -> r.getId().equalsIgnoreCase(finalChunkDTO.getId())).findFirst();

            if(optionalRegion.isEmpty()) return;
            region = optionalRegion.get();

            WorldguardSyncManager.setRegionMembersAndOwner(region,
                    !hasExpired ? landDTO.getOwnerId() : null,
                    !hasExpired ? landDTO.getMemberIDs() : null
            ); // Updates the region
        }

        region.setFlag(Main.BARBARIAN_BUILD, StateFlag.State.ALLOW); // Allow barbarians to build on regions of players

        main.getOverworldRegionManager().save(); // Don't forget to save the region
        Log.success(chunk.getX() + "/" + chunk.getZ() + " is synced");
    }

    public static void showParticleEffectAtChunk(Chunk chunk, Location location, ParticleData particleData) {
        List<Location> bounds = WorldguardSyncManager.getChunkBounds(chunk, location.getY() + 1.5);
        List<Object> packets = new ArrayList<>();
        ParticleBuilder particle = new ParticleBuilder(ParticleEffect.DUST_COLOR_TRANSITION)
                .setParticleData(particleData);
        for(Location l : bounds) {
            packets.add(particle.setLocation(l).toPacket());
        }
        int task = TaskManager.startWorldTask(packets, 5, chunk.getWorld());

        Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> {
            // code to execute after 10 seconds goes here
            TaskManager.getTaskManager().stopTask(task);
        }, 600L); // 30 seconds
    }
    
    public static void showParticleEffectAtChunk(Chunk chunk, Location location, ChunkAPI.ChunkState chunkState) {
        ParticleData particleData;
        switch (chunkState) {
            case BLACKLISTED -> {
                particleData = new DustData(0, 0, 0, 20); // Black
            }
            case UNCLAIMABLE -> {
                particleData = new DustData(255, 0, 0, 20); // Red
            }
            case UNCLAIMED -> {
                particleData = new DustData(0, 128, 0, 20); // Green
            }
            case CLAIMED_EXPIRABLE -> {
                particleData = new DustData(255, 255, 0, 20); // Yellow
            }
            case CLAIMED_PERMANENT -> {
                particleData = new DustData(0, 0, 255, 20); // Blue
            }
            default -> {
                particleData = new DustData(255, 255, 255, 20); // White
            }
        }

        showParticleEffectAtChunk(chunk, location, particleData);
    }

}
