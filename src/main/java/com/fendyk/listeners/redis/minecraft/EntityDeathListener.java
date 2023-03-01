package com.fendyk.listeners.redis.minecraft;

import com.fendyk.API;
import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.ActivityDTO;
import com.fendyk.DTOs.updates.UpdateActivitiesDTO;
import com.fendyk.Main;
import com.fendyk.configs.EarningsConfig;
import com.fendyk.managers.ActivityEarningsManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Optional;

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

        ActivitiesDTO activitiesDTO = server.getApi().getActivitiesAPI().getRedis().get(killer.getUniqueId());

        if(activitiesDTO == null) return;

        UpdateActivitiesDTO updateActivitiesDTO = new UpdateActivitiesDTO();
        ArrayList<ActivityDTO> activities = new ArrayList<>();
        ActivityDTO activity = new ActivityDTO();

        if(killed instanceof Player) {
            Optional<ActivityDTO> pvpActivity = activitiesDTO.getPvp().stream().filter(activity1 -> activity1.getName().equals(killed.getUniqueId().toString())).findFirst();

            double amount = pvpActivity.map(
                    activityDTO -> ActivityEarningsManager.getEarningsFromPvp((int) activityDTO.quantity, 1))
                    .orElseGet(() -> ActivityEarningsManager.getEarningsFromPvp(1, 1));

            activity.setName(killed.getUniqueId().toString());
            activity.setEarnings(amount);
            activity.setQuantity(1);
            activities.add(activity);
            updateActivitiesDTO.setPvp(activities);

            api.getMinecraftUserAPI().depositBalance(killer.getUniqueId(), new BigDecimal(amount));
            api.getActivitiesAPI().getFetch().update(killer.getUniqueId(), updateActivitiesDTO);

            killer.sendMessage("You have killed " + killed.getName() + " and received " + amount + " quanta");
        }
        else if(config.getEntities().containsKey(killed.getType().name())) {
            Optional<ActivityDTO> pvpActivity = activitiesDTO.getPve().stream().filter(activity1 -> activity1.getName().equals(killed.getType().name())).findFirst();

            double amount = pvpActivity.map(
                            activityDTO -> ActivityEarningsManager.getEarningsFromPve(killed.getType(), (int) activityDTO.quantity, 1))
                    .orElseGet(() -> ActivityEarningsManager.getEarningsFromPve(killed.getType(), 1, 1));

            activity.setName(killed.getType().name());
            activity.setEarnings(amount);
            activity.setQuantity(1);
            activities.add(activity);
            updateActivitiesDTO.setPve(activities);

            api.getMinecraftUserAPI().depositBalance(killer.getUniqueId(), new BigDecimal(amount));
            api.getActivitiesAPI().getFetch().update(killer.getUniqueId(), updateActivitiesDTO);

            killer.sendMessage("You have killed an " + killed.getType() + " and received " + amount + " quanta");
        }
    }

}
