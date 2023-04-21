package com.fendyk.listeners.minecraft;

import com.fendyk.API;
import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.ActivityDTO;
import com.fendyk.DTOs.updates.UpdateActivitiesDTO;
import com.fendyk.Main;
import com.fendyk.configs.EarningsConfig;
import com.fendyk.managers.ActivityBossBarManager;
import com.fendyk.utilities.ActivityEarnings;
import com.fendyk.managers.ActivitySoundManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.math.BigDecimal;
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
        Player killer = event.getEntity().getKiller();

        EarningsConfig config = server.getEarningsConfig();
        API api = server.getApi();

        if(killer == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(server, () -> {
            UpdateActivitiesDTO updateActivitiesDTO = new UpdateActivitiesDTO();
            ArrayList<ActivityDTO> activities = new ArrayList<>();
            ActivityDTO activity = new ActivityDTO();

            if(killed instanceof Player) {
                ActivitiesDTO activitiesDTO = server.getApi().getActivitiesAPI().redis.get(killer.getUniqueId());
                double amount = 0;
                if(activitiesDTO != null) {
                    Optional<ActivityDTO> pvpActivity = activitiesDTO.pvp.stream().filter(activity1 -> activity1.name.equals(killed.getUniqueId().toString())).findFirst();

                    if(pvpActivity.isPresent()) {
                        amount = pvpActivity.map(
                                activityDTO ->  ActivityEarnings.getEarningsFromPvp((int) activityDTO.quantity, 1)).get();
                    }
                    else {
                        amount = ActivityEarnings.getEarningsFromPvp(1, 1);
                    }
                }
                else {
                    amount = ActivityEarnings.getEarningsFromPvp(1, 1);
                }

                activity.name = killed.getUniqueId().toString();
                activity.earnings = amount;
                activity.quantity = 1;
                activities.add(activity);
                updateActivitiesDTO.setPvp(activities);

                api.getMinecraftUserAPI().depositBalance(killer, new BigDecimal(amount));
                ActivitiesDTO updatedActivities = api.getActivitiesAPI().update(killer, updateActivitiesDTO);

                ActivityBossBarManager.showBossBar(killer, activity, ActivityBossBarManager.Type.PVP);
                ActivitySoundManager.play(killer);
                killer.sendActionBar(
                        Component.text("+ " + String.format("%.2f", amount) + " $QTA")
                                .color(NamedTextColor.GREEN)
                );

                if(updatedActivities != null) {
                    Optional<ActivityDTO> optional = updatedActivities.pvp.stream().filter(item -> item.name.equalsIgnoreCase(killed.getUniqueId().toString())).findFirst();
                    optional.ifPresent(activityDTO -> ActivityBossBarManager.showBossBar(killer, activityDTO, ActivityBossBarManager.Type.PVP));
                }
            }
            else if(config.getEntityEarnings().containsKey(killed.getType())) {
                ActivitiesDTO activitiesDTO = server.getApi().getActivitiesAPI().redis.get(killer.getUniqueId());
                double amount = 0;
                if(activitiesDTO != null) {
                    Optional<ActivityDTO> pvpActivity = activitiesDTO.pve.stream().filter(item -> item.name.equalsIgnoreCase(killed.getType().name())).findFirst();
                    amount = pvpActivity.map(activityDTO -> ActivityEarnings.getEarningsFromPve(killed.getType(), (int) activityDTO.quantity, 1))
                            .orElseGet(() -> ActivityEarnings.getEarningsFromPve(killed.getType(), 1, 1));
                }
                else {
                    amount = ActivityEarnings.getEarningsFromPve(killed.getType(), 1, 1);
                }

                activity.name = killed.getType().name();
                activity.earnings = amount;
                activity.quantity = 1;
                activities.add(activity);
                updateActivitiesDTO.setPve(activities);

                api.getMinecraftUserAPI().depositBalance(killer, new BigDecimal(amount));
                ActivitiesDTO updatedActivities = api.getActivitiesAPI().update(killer, updateActivitiesDTO);

                ActivityBossBarManager.showBossBar(killer, activity, ActivityBossBarManager.Type.PVE);
                ActivitySoundManager.play(killer);
                killer.sendActionBar(
                        Component.text("+ " + String.format("%.2f", amount) + " $QTA has been added to your account.")
                                .color(NamedTextColor.GREEN)
                );

                if(updatedActivities != null) {
                    Optional<ActivityDTO> optional = updatedActivities.pve.stream().filter(item -> item.name.equalsIgnoreCase(killed.getType().name())).findFirst();
                    optional.ifPresent(activityDTO -> ActivityBossBarManager.showBossBar(killer, activityDTO, ActivityBossBarManager.Type.PVE));
                }
            }

        });

    }

}
