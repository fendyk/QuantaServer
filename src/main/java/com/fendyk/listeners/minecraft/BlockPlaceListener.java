package com.fendyk.listeners.minecraft;

import com.fendyk.DTOs.BlacklistedBlockDTO;
import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.updates.UpdateChunkDTO;
import com.fendyk.Main;
import com.fendyk.configs.EarningsConfig;
import com.fendyk.utilities.extentions.WorldGuardExtension;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BlockPlaceListener implements Listener {
    Main server;
    Main main = Main.getInstance();

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

        // If the current user is either barbarian or default, verify the flag.
        if(!WorldGuardExtension.hasPermissionToBuildAtGlobalLocation(player, block.getLocation())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You are not allowed to build.");
            return;
        }

        // Ignore if player is Operator
        if(player.isOp()) return;

        // If material is supported.
        if(config.getMaterialEarnings().containsKey(material)) {

            player.sendMessage(ChatColor.RED + "Placing " + material.name() + " is currently disabled until we found a good solution to prevent $QTA generation. Stay tuned :)");
            event.setCancelled(true);
            return;

            // If we're not in the 'normal world', disable block place at ALL times
            // Cannot earn
            /*
            if(!player.getWorld().getName().equalsIgnoreCase(server.getServerConfig().getWorldName())) {
                player.sendMessage("You're only allowed to place ore blocks in the world.");
                event.setCancelled(true);
                return;
            }

            CompletableFuture.runAsync(() -> {
                try {
                    // Verify if the chunk is blacklisted
                    // Check if the player is within the blacklisted chunk radius
                    if(main.getServerConfig().isWithinBlacklistedChunkRadius(player.getLocation())) {
                        player.sendMessage("The chunk you're currently standing on is considered 'blacklisted' and not claimable.");
                        return;
                    }

                    // Find the chunk
                    ChunkDTO chunkDTO = server.getApi().getChunkAPI().get(chunk);

                    // Create chunk if not found
                    if (chunkDTO == null) {
                        chunkDTO = server.getApi().getChunkAPI().create(chunk, true);

                        if (chunkDTO == null) return;
                    }

                    // Blacklist block by updating and pushing block location into chunk
                    BlacklistedBlockDTO b = new BlacklistedBlockDTO(block.getX(), block.getY(), block.getZ());
                    UpdateChunkDTO update = new UpdateChunkDTO();
                    update.getPushBlacklistedBlocks().add(b);
                    server.getApi().getChunkAPI().update(chunk, update);
                } catch (Exception e) {
                    player.sendMessage(e.getMessage());
                }
            });
             */

        }
    }

}
