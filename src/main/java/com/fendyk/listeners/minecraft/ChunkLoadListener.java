package com.fendyk.listeners.minecraft;

import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.Main;
import com.fendyk.managers.WorldguardSyncManager;
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
        Bukkit.getScheduler().runTaskAsynchronously(server, () -> {
            if(!event.getWorld().getName().equalsIgnoreCase(worldName)) return;
            Chunk chunk = event.getChunk();
            final String key = chunk.getX() + ":" + chunk.getZ();

            if (!checkedChunks.containsKey(key)) {
                Bukkit.getLogger().info(chunk.getX() + "/" + chunk.getZ() + " loaded");

                Bukkit.getScheduler().runTask(server, () -> {
                    try {
                        WorldguardSyncManager.syncChunkWithRegion(chunk, null, null);
                    } catch (StorageException e) {
                        throw new RuntimeException(e);
                    }
                    checkedChunks.put(key, chunk);
                });
            }
        });
    }

}
