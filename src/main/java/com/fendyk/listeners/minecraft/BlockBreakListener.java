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
import com.fendyk.managers.ActivityBossbarManager;
import com.fendyk.managers.ActivityEarningsManager;
import com.fendyk.managers.ActivitySoundManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
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

    public BlockBreakListener(Main server) {
        this.server = server;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws IOException {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        Material material = block.getType();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        Location location = block.getLocation();
        API api = server.getApi();
        EarningsConfig config = server.getEarningsConfig();

        // Player can only receive the block IF in survival mode, unless you are operator
        if(player.getGameMode() != GameMode.SURVIVAL && !player.isOp()) return;

        // If the player uses silk touch, we simply deny
        if(itemStack.containsEnchantment(Enchantment.SILK_TOUCH)) return;

        // Check if Material is supported
        if(!config.getMaterialEarnings().containsKey(material)) return;

        ChunkDTO chunkDTO = server.getApi().getChunkAPI().get(chunk);

        if(chunkDTO != null) {
            if(ChunkAPI.isBlacklistedBlock(chunkDTO, block)) {
                UpdateChunkDTO update = new UpdateChunkDTO();
                update.getSpliceBlacklistedBlocks().add(new BlacklistedBlockDTO(block));
                server.getApi().getChunkAPI().update(chunk, update);
                return;
            }
        }

        ActivitiesDTO activitiesDTO = server.getApi().getActivitiesAPI().getRedis().get(player.getUniqueId());
        double amount = 0;

        if(activitiesDTO == null) {
            amount = ActivityEarningsManager.getEarningsFromMining(material, 1, 1);
        }
        else {
            Optional<ActivityDTO> pvpActivity = activitiesDTO.getMining().stream().filter(activity1 -> activity1.getName().equals(material.name())).findFirst();

            if(pvpActivity.isPresent()) {
                amount = pvpActivity.map(
                        activityDTO -> ActivityEarningsManager.getEarningsFromMining(material, (int) activityDTO.getQuantity(), 1)).get();
            }
            else {
                amount = ActivityEarningsManager.getEarningsFromMining(material, 1, 1);
            }
        }

        UpdateActivitiesDTO updateActivitiesDTO = new UpdateActivitiesDTO();
        ArrayList<ActivityDTO> activities = new ArrayList<>();
        ActivityDTO activity = new ActivityDTO();
        activity.setName(material.name());
        activity.setEarnings(amount);
        activity.setQuantity(1);
        activities.add(activity);
        updateActivitiesDTO.setMining(activities);

        api.getMinecraftUserAPI().depositBalance(player.getUniqueId(), new BigDecimal(amount));
        ActivitiesDTO updatedActivities = api.getActivitiesAPI().getFetch().update(player.getUniqueId(), updateActivitiesDTO);

        player.sendMessage(
                Component.text("+ " + String.format("%.2f", amount) + " $QTA")
                        .color(NamedTextColor.GREEN)
        );

        if(updatedActivities != null) {
            Optional<ActivityDTO> optional = updatedActivities.getMining().stream().filter(item -> item.getName().equalsIgnoreCase(material.name())).findFirst();
            optional.ifPresent(activityDTO -> ActivityBossbarManager.showBossBar(player, activityDTO, ActivityBossbarManager.Type.MINING));
        }
        ActivitySoundManager.play(player);
    }

}