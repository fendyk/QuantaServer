package com.fendyk.listeners.redis.minecraft;

import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.Main;
import com.fendyk.utilities.Vector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.RegionResultSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
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

public class ChunkLoadListener implements Listener {

    HashMap<String, Chunk> checkedChunks;
    Main server;

    String worldName;

    public ChunkLoadListener(Main server) {
        this.server = server;
        this.checkedChunks = new HashMap<>();

        this.worldName = server.getTomlConfig().getString("worldName");
    }

    /*
    @EventHandler
    public void onChunkUnload(PlayerChunkUnloadEvent event) {
        if(!event.getWorld().getName().equalsIgnoreCase(worldName)) return;
        Chunk chunk = event.getChunk();
        String key = chunk.getX() + ":" + chunk.getZ();
        if(!checkedChunks.containsKey(key)) return;

        Bukkit.getLogger().info(chunk.getX() + "/" + chunk.getZ() + " unloaded");

        checkedChunks.remove(key);
    }
     */

    //@EventHandler
    //public void onChunkLoad(PlayerChunkLoadEvent event) {
        //if(!event.getWorld().getName().equalsIgnoreCase(worldName)) return;
        //Chunk chunk = event.getChunk();
        //String key = chunk.getX() + ":" + chunk.getZ();
        //if(checkedChunks.containsKey(key)) return;

        //Bukkit.getLogger().info(chunk.getX() + "/" + chunk.getZ() + " loaded");

        /* To avoid unnecessary calls to the api, first check if we already CACHED the chunk  */
        //boolean isCached = server.getApi().getChunkAPI().getRedis().exists(new Vector2(chunk.getX(), chunk.getZ()));

        //if(!isCached) {
            //Bukkit.getLogger().info(chunk.getX() + "/" + chunk.getZ() + " needs an verification");
            //ChunkDTO chunkDTO = server.getApi().getChunkAPI().get(chunk, true);
            //if(chunkDTO == null) return; // Could not find so no need for check

            // Get the landId to see if

            //LandDTO land = server.getApi().getLandAPI().get(chunkDTO.getLandId());

            //Location center = getChunkCenter(chunk);

            // Find the worldguard region
            // If found, check if the WorldGuard owner is same as in DTO
            // If changed, update
            //Set<ProtectedRegion> set = server.getRegionManager().getApplicableRegions(
                    //BlockVector3.at(center.x(), center.y(), center.z())
            //).getRegions();

            //new RegionResultSet(set, null);

            //checkedChunks.put(key, chunk); // We've checked this region so no need for a re-check
        //}
    //}

    public Location getChunkCenter(Chunk chunk) {
        int x = chunk.getX() << 4 + 8; // calculate the X coordinate of the center of the chunk
        int z = chunk.getZ() << 4 + 8; // calculate the Z coordinate of the center of the chunk
        int y = chunk.getWorld().getHighestBlockYAt(x, z); // get the highest block Y coordinate at the center of the chunk
        return new Location(chunk.getWorld(), x, y, z); // create and return a new Location object with the center coordinates
    }
    public ProtectedRegion getWorldGuardRegion(String landId) {
        ProtectedRegion region = server.getRegionManager().getRegion(landId);
        return region;
    }

    public BlockVector3 getChunkMin(Chunk chunk, int z) {
        Location topLeft = chunk.getBlock(0,-64,0).getLocation();
        return BlockVector3.at(topLeft.getX(), -256, topLeft.getZ());
    }


    public BlockVector3 getChunkMax(Chunk chunk, int z) {
        Location bottomRight = chunk.getBlock(15,320,15).getLocation();
        return BlockVector3.at(bottomRight.getX(), 256, bottomRight.getZ());
    }

    public void setWorldGuardRegion(@NotNull OfflinePlayer owner, @NotNull OfflinePlayer[] members, @NotNull Chunk chunk) {
        // Check if the region does not exists, if so, make it.
        ProtectedRegion region =  null;//getWorldGuardRegion();
        if(region == null) {
            Location topLeft = chunk.getBlock(0,-64,0).getLocation();
            Location bottomRight = chunk.getBlock(15,320,15).getLocation();

            BlockVector3 min = BlockVector3.at(topLeft.getX(), -256, topLeft.getZ());
            BlockVector3 max = BlockVector3.at(bottomRight.getX(), 256, bottomRight.getZ());
            region = new ProtectedCuboidRegion(chunk.getX() + "," + chunk.getZ(), min, max);
        }

        DefaultDomain newOwners = new DefaultDomain();
        DefaultDomain newMembers = new DefaultDomain();

        for(OfflinePlayer member : members) {
            newMembers.addPlayer(member.getUniqueId());
        }

        newOwners.addPlayer(owner.getUniqueId());

        region.setOwners(newOwners);
        region.setMembers(newMembers);
        server.getRegionManager().addRegion(region);
    }


}
