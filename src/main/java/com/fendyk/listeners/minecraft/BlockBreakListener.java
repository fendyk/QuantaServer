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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

public class BlockBreakListener implements Listener {
    Main main = Main.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.isCancelled()) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        Material material = block.getType();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        API api = main.getApi();
        EarningsConfig config = main.getEarningsConfig();

        // If the current user is either barbarian or default, verify the flag.
        if(!WorldGuardExtension.hasPermissionToBuildAtGlobalLocation(player, block.getLocation())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.DARK_RED + "You are not allowed to build.");
            return;
        }


        // Player can only receive the block IF in survival mode, unless you are operator
        if(player.getGameMode() != GameMode.SURVIVAL && !player.isOp()) return;

        // If the player uses silk touch, we simply deny
        if(itemStack.containsEnchantment(Enchantment.SILK_TOUCH)) return;

        // Check if Material is supported
        if(!config.getMaterialEarnings().containsKey(material)) return;

        ChunkDTO chunkDTO = main.getApi().getChunkAPI().get(chunk);

        if (chunkDTO != null) {
            if (ChunkAPI.isBlacklistedBlock(chunkDTO, block)) {
                UpdateChunkDTO update = new UpdateChunkDTO();
                update.getSpliceBlacklistedBlocks().add(new BlacklistedBlockDTO(block));
                main.getApi().getChunkAPI().update(chunk, update);
                return;
            }
        }

        ActivitiesDTO activitiesDTO = main.getApi().getActivitiesAPI().get(player);
        double amount;

        if (activitiesDTO == null) {
            amount = ActivityEarnings.getEarningsFromMining(material, 1, 1);
        } else {
            Optional<ActivityDTO> pvpActivity = activitiesDTO.getMining().stream().filter(activity1 -> activity1.getName().equals(material.name())).findFirst();
            amount = pvpActivity.map(activityDTO -> ActivityEarnings.getEarningsFromMining(material, (int) activityDTO.getQuantity() + 1, 1))
                    .orElseGet(() -> ActivityEarnings.getEarningsFromMining(material, 1, 1));
        }

        UpdateActivitiesDTO updateActivitiesDTO = new UpdateActivitiesDTO();
        ArrayList<ActivityDTO> activities = new ArrayList<>();
        ActivityDTO activity = new ActivityDTO();
        activity.setName(material.name());
        activity.setEarnings(new BigDecimal(amount).floatValue());
        activity.setQuantity(1);
        activities.add(activity);
        updateActivitiesDTO.setMining(activities);

        api.getMinecraftUserAPI().depositBalance(player, new BigDecimal(amount));
        ActivitiesDTO updatedActivities = api.getActivitiesAPI().update(player, updateActivitiesDTO);

        player.sendActionBar(
                Component.text("+" + String.format("%.8f", amount) + " $QTA")
                        .color(NamedTextColor.GREEN)
        );

        if (updatedActivities != null) {
            Optional<ActivityDTO> optional = updatedActivities.getMining().stream().filter(item -> item.getName().equalsIgnoreCase(material.name())).findFirst();
            optional.ifPresent(activityDTO -> ActivityBossBarManager.showBossBar(player, activityDTO, ActivityBossBarManager.Type.MINING));
        }
        ActivitySoundManager.play(player);
    }

}