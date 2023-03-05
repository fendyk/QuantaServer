package com.fendyk.listeners.redis.minecraft;
import com.fendyk.API;
import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.ActivityDTO;
import com.fendyk.DTOs.updates.UpdateActivitiesDTO;
import com.fendyk.Main;
import com.fendyk.configs.EarningsConfig;
import com.fendyk.managers.ActivityEarningsManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

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
        Material material = block.getType();
        Location location = block.getLocation();
        API api = server.getApi();
        EarningsConfig config = server.getEarningsConfig();


        // A player will not earn on a block that has been blacklisted
        // Remove the block if so.
        /*
        if(Main.getBlockBlacklistManager().isBlacklisted(location)) {
            Main.getBlockBlacklistManager().removeAndSync(location, false);
            return;
        }

         */

        // Player can only receive the block IF in survival mode, unless you are operator
        if(player.getGameMode() != GameMode.SURVIVAL && !player.isOp()) return;

        // Check if supported, and claim the ore if so.
        if(config.getMaterials().containsKey(material.name())) {

            Bukkit.getConsoleSender().sendMessage("uuid: " + player.getUniqueId());
            Bukkit.getConsoleSender().sendMessage(player.getName() + " mined");

            ActivitiesDTO activitiesDTO = server.getApi().getActivitiesAPI().getRedis().get(player.getUniqueId());

            // If we dont find the DTO, set default. The route will create an activity by itself
            double amount = 0;
            if(activitiesDTO != null) {
                Optional<ActivityDTO> pvpActivity = activitiesDTO.getMining().stream().filter(activity1 -> activity1.getName().equals(material.name())).findFirst();

                if(pvpActivity.isPresent()) {
                    amount = pvpActivity.map(
                            activityDTO -> ActivityEarningsManager.getEarningsFromMining(material, (int) activityDTO.getQuantity(), 1)) .get();
                }
                else {
                    amount = ActivityEarningsManager.getEarningsFromMining(material, 1, 1);
                }
            }
            else {
                amount = ActivityEarningsManager.getEarningsFromMining(material, 1, 1);
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
            api.getActivitiesAPI().getFetch().update(player.getUniqueId(), updateActivitiesDTO);

            player.sendMessage("You have mined an " + material.name() + " and received " + amount + " quanta");
        }
    }

}