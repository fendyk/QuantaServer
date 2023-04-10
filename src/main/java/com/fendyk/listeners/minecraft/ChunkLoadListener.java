package com.fendyk.listeners.minecraft;

import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.Main;
import com.fendyk.managers.WorldguardSyncManager;
import com.fendyk.utilities.ChunkUtils;
import com.fendyk.utilities.Log;
import com.fendyk.utilities.Vector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class ChunkLoadListener implements Listener {

    Main main = Main.getInstance();
    HashMap<String, Chunk> checkedChunks;
    Main server;

    String worldName;

    public ChunkLoadListener(Main server) {
        this.server = server;
        this.checkedChunks = new HashMap<>();

        this.worldName = server.getServerConfig().getWorldName();
    }

    /**
     * We need to verify if chunks are up-to-date.
     * If it does not exist, we can simply return.
     * We keep track of the chunks that are verified
     * @param event
     */
    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent event) {
        // Check if the event is for the specified world
        if (!event.getWorld().getName().equalsIgnoreCase(worldName)) return;

        // Get the chunk
        Chunk chunk = event.getChunk();

        // Create a chunk key
        final String key = chunk.getX() + ":" + chunk.getZ();

        // Check if the chunk is already processed
        if (checkedChunks.containsKey(key)) return;

        // Run the async task
        Bukkit.getScheduler().runTaskAsynchronously(server, () -> {
            Bukkit.getLogger().info(chunk.getX() + "/" + chunk.getZ() + " loaded");

            // Check if the player is within the blacklisted chunk radius
            if(main.getServerConfig().isWithinBlacklistedChunkRadius(ChunkUtils.getChunkCenter(chunk))) {
                Bukkit.getLogger().info(chunk.getX() + "/" + chunk.getZ() + " chunk is considered blacklisted, no need for check.");
                return;
            }

            // Run the sync task
            Bukkit.getScheduler().runTask(server, () -> {
                try {
                    WorldguardSyncManager.syncChunkWithRegion(chunk, null, null);
                    checkedChunks.put(key, chunk);
                } catch (StorageException e) {
                    Bukkit.getLogger().severe("StorageException occurred while syncing chunk with region: " + e.getMessage());
                }
            });
        });
    }

}
