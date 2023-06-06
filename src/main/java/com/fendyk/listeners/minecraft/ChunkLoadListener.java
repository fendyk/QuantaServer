package com.fendyk.listeners.minecraft;

import com.fendyk.Main;
import com.fendyk.managers.WorldguardSyncManager;
import com.fendyk.utilities.ChunkUtils;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class ChunkLoadListener implements Listener {

    Main main = Main.getInstance();
    HashMap<String, Chunk> checkedChunks;
    String worldName;

    public ChunkLoadListener() {
        this.checkedChunks = new HashMap<>();
        this.worldName = main.getServerConfig().getWorldName();
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
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            Bukkit.getLogger().info(chunk.getX() + "/" + chunk.getZ() + " loaded");
            checkedChunks.put(key, chunk);

            // Check if the player is within the blacklisted chunk radius
            if(main.getServerConfig().isWithinBlacklistedChunkRadius(ChunkUtils.getChunkCenter(chunk))) {
                Bukkit.getLogger().info(chunk.getX() + "/" + chunk.getZ() + " chunk is considered blacklisted, no need for check.");
                return;
            }

            // Run the sync task
            try {
                WorldguardSyncManager.syncChunkWithRegion(chunk, null, null);
            } catch (StorageException e) {
                Bukkit.getLogger().severe("StorageException occurred while syncing chunk with region: " + e.getMessage());
            }
        });
    }

}
