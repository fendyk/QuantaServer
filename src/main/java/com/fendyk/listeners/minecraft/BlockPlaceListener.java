package com.fendyk.listeners.minecraft;

import com.fendyk.Main;
import com.fendyk.configs.EarningsConfig;
import com.fendyk.utilities.extentions.WorldGuardExtension;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    Main main = Main.getInstance();

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material material = block.getType();
        EarningsConfig config = main.getEarningsConfig();

        // If the current user is either barbarian or default, verify the flag.
        if (!WorldGuardExtension.hasPermissionToBuildAtGlobalLocation(player, block.getLocation())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You are not allowed to build.");
            return;
        }

        // Ignore if player is Operator
        if (player.isOp()) return;

        // If material is supported.
        if (config.getMaterialEarnings().containsKey(material)) {

            player.sendMessage(ChatColor.RED + "Placing " + material.name() + " is currently disabled until we found a good solution to prevent $QTA generation. Stay tuned :)");
            event.setCancelled(true);

            // If we're not in the 'normal world', disable block place at ALL times
            // Cannot earn
            /*
            if(!player.getWorld().getName().equalsIgnoreCase(main.getServerConfig().getWorldName())) {
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
                    ChunkDTO chunkDTO = main.getApi().getChunkAPI().get(chunk);

                    // Create chunk if not found
                    if (chunkDTO == null) {
                        chunkDTO = main.getApi().getChunkAPI().create(chunk, true);

                        if (chunkDTO == null) return;
                    }

                    // Blacklist block by updating and pushing block location into chunk
                    BlacklistedBlockDTO b = new BlacklistedBlockDTO(block.getX(), block.getY(), block.getZ());
                    UpdateChunkDTO update = new UpdateChunkDTO();
                    update.getPushBlacklistedBlocks().add(b);
                    main.getApi().getChunkAPI().update(chunk, update);
                } catch (Exception e) {
                    player.sendMessage(e.getMessage());
                }
            });
             */

        }
    }

}
