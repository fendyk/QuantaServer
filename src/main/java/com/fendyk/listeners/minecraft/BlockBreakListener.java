package com.fendyk.listeners.minecraft;
import com.fendyk.API;
import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.ActivityDTO;
import com.fendyk.DTOs.BlacklistedBlockDTO;
import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.updates.UpdateActivitiesDTO;
import com.fendyk.DTOs.updates.UpdateChunkDTO;
import com.fendyk.Main;
import com.fendyk.clients.apis.ChunkAPI;
import com.fendyk.configs.EarningsConfig;
import com.fendyk.managers.ActivityBossBarManager;
import com.fendyk.utilities.ActivityEarnings;
import com.fendyk.managers.ActivitySoundManager;
import com.fendyk.utilities.extentions.WorldGuardExtension;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

public class BlockBreakListener implements Listener {

    Main server;
    Main main = Main.instance;

    public BlockBreakListener(Main server) {
        this.server = server;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws IOException {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        Material material = block.getType();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        API api = server.api;
        EarningsConfig config = server.earningsConfig;

        // If the current user is either barbarian or default, verify the flag.
        if(!WorldGuardExtension.hasPermissionToBuildAtGlobalLocation(player, block.getLocation())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You are not allowed to build.");
            return;
        }


        // Player can only receive the block IF in survival mode, unless you are operator
        if(player.getGameMode() != GameMode.SURVIVAL && !player.isOp()) return;

        // If the player uses silk touch, we simply deny
        if(itemStack.containsEnchantment(Enchantment.SILK_TOUCH)) return;

        // Check if Material is supported
        if(!config.getMaterialEarnings().containsKey(material)) return;

        Bukkit.getScheduler().runTaskAsynchronously(server, () -> {
            ChunkDTO chunkDTO = server.api.chunkAPI.get(chunk);

            if (chunkDTO != null) {
                if (ChunkAPI.isBlacklistedBlock(chunkDTO, block)) {
                    UpdateChunkDTO update = new UpdateChunkDTO();
                    update.spliceBlacklistedBlocks.add(new BlacklistedBlockDTO(block));
                    server.api.chunkAPI.update(chunk, update);
                    return;
                }
            }

            ActivitiesDTO activitiesDTO = server.api.activitiesAPI.redis.get(player.getUniqueId());
            double amount;

            if (activitiesDTO == null) {
                amount = ActivityEarnings.getEarningsFromMining(material, 1, 1);
            } else {
                Optional<ActivityDTO> pvpActivity = activitiesDTO.mining.stream().filter(activity1 -> activity1.name.equals(material.name())).findFirst();
                amount = pvpActivity.map(activityDTO -> ActivityEarnings.getEarningsFromMining(material, (int) activityDTO.quantity + 1, 1))
                        .orElseGet(() -> ActivityEarnings.getEarningsFromMining(material, 1, 1));
            }

            UpdateActivitiesDTO updateActivitiesDTO = new UpdateActivitiesDTO();
            ArrayList<ActivityDTO> activities = new ArrayList<>();
            ActivityDTO activity = new ActivityDTO();
            activity.name = material.name();
            activity.earnings = amount;
            activity.quantity = 1;
            activities.add(activity);
            updateActivitiesDTO.setMining(activities);

            api.minecraftUserAPI.depositBalance(player, new BigDecimal(amount));
            ActivitiesDTO updatedActivities = api.activitiesAPI.fetch.update(player.getUniqueId(), updateActivitiesDTO);

            player.sendActionBar(
                    Component.text("+" + String.format("%.8f", amount) + " $QTA")
                            .color(NamedTextColor.GREEN)
            );

            if (updatedActivities != null) {
                Optional<ActivityDTO> optional = updatedActivities.mining.stream().filter(item -> item.name.equalsIgnoreCase(material.name())).findFirst();
                optional.ifPresent(activityDTO -> ActivityBossBarManager.showBossBar(player, activityDTO, ActivityBossBarManager.Type.MINING));
            }
            ActivitySoundManager.play(player);

        });
    }

}