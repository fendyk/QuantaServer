package com.fendyk.listeners.redis.minecraft;

import com.fendyk.API;
import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.ActivityDTO;
import com.fendyk.DTOs.updates.UpdateActivitiesDTO;
import com.fendyk.Main;
import com.fendyk.configs.EarningsConfig;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class EntityDeathListener implements Listener {

    Main server;

    public EntityDeathListener(Main server) {
        this.server = server;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity killed = event.getEntity();
        Entity killer = event.getEntity().getKiller();

        EarningsConfig config = server.getEarningsConfig();
        API api = server.getApi();

        if(killer == null) return;

        UpdateActivitiesDTO updateActivitiesDTO = new UpdateActivitiesDTO();

        // TODO: Find the amount the player already has killed.

        if(killed instanceof Player) {
            BigDecimal amount  = config.getPlayerKillEarnings().setScale(2, RoundingMode.HALF_EVEN);
            ArrayList<ActivityDTO> activities = new ArrayList<>();
            ActivityDTO activity = new ActivityDTO();
            activity.setName(killed.getUniqueId().toString());
            activity.setEarnings(amount.longValue());
            activity.setQuantity(1);
            activities.add(activity);
            updateActivitiesDTO.setPvp(activities);

            api.getMinecraftUserAPI().depositBalance(killer.getUniqueId(), amount);
            api.getActivitiesAPI().update(killer.getUniqueId(), updateActivitiesDTO);

            killer.sendMessage("You have killed " + killed.getName() + " and received " + amount + "quanta");
        }
        else if(config.getEntities().containsKey(killed.getType().name())) {
            BigDecimal amount  = config.getEntityEarnings(killed.getType()).setScale(2, RoundingMode.HALF_EVEN);
            ArrayList<ActivityDTO> activities = new ArrayList<>();
            ActivityDTO activity = new ActivityDTO();
            activity.setName(killed.getType().name());
            activity.setEarnings(amount.longValue());
            activity.setQuantity(1);
            activities.add(activity);
            updateActivitiesDTO.setPvp(activities);

            api.getMinecraftUserAPI().depositBalance(killer.getUniqueId(), amount);
            api.getActivitiesAPI().update(killer.getUniqueId(), updateActivitiesDTO);

            killer.sendMessage("You have killed a " + killed.getType() + " and received " + amount + "quanta");
        }
    }

}
