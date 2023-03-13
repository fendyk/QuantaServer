package com.fendyk.listeners.minecraft;

import com.fendyk.DTOs.BlacklistedBlockDTO;
import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.updates.UpdateChunkDTO;
import com.fendyk.Main;
import com.fendyk.configs.EarningsConfig;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    Main server;
    public BlockPlaceListener(Main server) {
        this.server = server;
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        Material material = block.getType();

        EarningsConfig config = server.getEarningsConfig();

        // If material is supported.
        if(config.getMaterialEarnings().containsKey(material)) {

            // If we're not in the 'normal world', disable block place at ALL times
            // Cannot earn
            if(!player.getWorld().getName().equalsIgnoreCase(server.getServerConfig().getWorldName())) {
                player.sendMessage("You're only allowed to place ore blocks in the world.");
                event.setCancelled(true);
                return;
            }

            // Verify if the chunk is blacklisted
            if(server.getApi().getBlacklistedChunkAPI().isBlacklisted(chunk)) return;

            // Find the chunk
            ChunkDTO chunkDTO = server.getApi().getChunkAPI().get(chunk);

            // Create chunk if not found
            if(chunkDTO == null) {
                chunkDTO = server.getApi().getChunkAPI().create(chunk, true);

                if(chunkDTO == null) return;
            }

            // Blacklist block by updating and pushing block location into chunk
            BlacklistedBlockDTO b = new BlacklistedBlockDTO(block.getX(), block.getY(), block.getZ());
            UpdateChunkDTO update = new UpdateChunkDTO();
            update.getPushBlacklistedBlocks().add(b);
            server.getApi().getChunkAPI().update(chunk, update);

        }
    }

}
