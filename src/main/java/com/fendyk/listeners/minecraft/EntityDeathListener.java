package com.fendyk.listeners.minecraft;

import com.fendyk.API;
import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.ActivityDTO;
import com.fendyk.DTOs.updates.UpdateActivitiesDTO;
import com.fendyk.Main;
import com.fendyk.configs.EarningsConfig;
import com.fendyk.managers.ActivityBossbarManager;
import com.fendyk.managers.ActivityEarningsManager;
import com.fendyk.managers.ActivitySoundManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
        Player killer = event.getEntity().getKiller();

        EarningsConfig config = server.getEarningsConfig();
        API api = server.getApi();

        if(killer == null) return;

        ActivitiesDTO activitiesDTO = server.getApi().getActivitiesAPI().getRedis().get(killer.getUniqueId());
        UpdateActivitiesDTO updateActivitiesDTO = new UpdateActivitiesDTO();
        ArrayList<ActivityDTO> activities = new ArrayList<>();
        ActivityDTO activity = new ActivityDTO();

        if(killed instanceof Player) {
            double amount = 0;
            if(activitiesDTO != null) {
                Optional<ActivityDTO> pvpActivity = activitiesDTO.getPvp().stream().filter(activity1 -> activity1.getName().equals(killed.getUniqueId().toString())).findFirst();

                if(pvpActivity.isPresent()) {
                    amount = pvpActivity.map(
                            activityDTO ->  ActivityEarningsManager.getEarningsFromPvp((int) activityDTO.getQuantity(), 1)).get();
                }
                else {
                    amount = ActivityEarningsManager.getEarningsFromPvp(1, 1);
                }
            }
            else {
                amount = ActivityEarningsManager.getEarningsFromPvp(1, 1);
            }

            activity.setName(killed.getUniqueId().toString());
            activity.setEarnings(amount);
            activity.setQuantity(1);
            activities.add(activity);
            updateActivitiesDTO.setPvp(activities);

            api.getMinecraftUserAPI().depositBalance(killer.getUniqueId(), new BigDecimal(amount));
            ActivitiesDTO updatedActivities = api.getActivitiesAPI().getFetch().update(killer.getUniqueId(), updateActivitiesDTO);

            ActivityBossbarManager.showBossBar(killer, activity, ActivityBossbarManager.Type.PVP);
            ActivitySoundManager.play(killer);
            killer.sendMessage(
                    Component.text("+ " + String.format("%.2f", amount) + " $QTA")
                            .color(NamedTextColor.GREEN)
            );

            if(updatedActivities != null) {
                Optional<ActivityDTO> optional = updatedActivities.getPvp().stream().filter(item -> item.getName().equalsIgnoreCase(killed.getUniqueId().toString())).findFirst();
                optional.ifPresent(activityDTO -> ActivityBossbarManager.showBossBar(killer, activityDTO, ActivityBossbarManager.Type.PVP));
            }
        }
        else if(config.getEntityEarnings().containsKey(killed.getType())) {
            double amount = 0;
            if(activitiesDTO != null) {
                Optional<ActivityDTO> pvpActivity = activitiesDTO.getPve().stream().filter(activity1 -> activity1.getName().equals(killed.getType().name())).findFirst();

                if(pvpActivity.isPresent()) {
                    amount = pvpActivity.map(
                            activityDTO -> ActivityEarningsManager.getEarningsFromPve(killed.getType(), (int) activityDTO.getQuantity(), 1)).get();
                }
                else {
                    amount = ActivityEarningsManager.getEarningsFromPve(killed.getType(), 1, 1);
                }
            }
            else {
                amount = ActivityEarningsManager.getEarningsFromPve(killed.getType(), 1, 1);
            }

            activity.setName(killed.getType().name());
            activity.setEarnings(amount);
            activity.setQuantity(1);
            activities.add(activity);
            updateActivitiesDTO.setPve(activities);

            api.getMinecraftUserAPI().depositBalance(killer.getUniqueId(), new BigDecimal(amount));
            ActivitiesDTO updatedActivities = api.getActivitiesAPI().getFetch().update(killer.getUniqueId(), updateActivitiesDTO);

            ActivityBossbarManager.showBossBar(killer, activity, ActivityBossbarManager.Type.PVE);
            ActivitySoundManager.play(killer);
            killer.sendMessage(
                    Component.text("+ " + String.format("%.2f", amount) + " $QTA")
                            .color(NamedTextColor.GREEN)
            );

            if(updatedActivities != null) {
                Optional<ActivityDTO> optional = updatedActivities.getPve().stream().filter(item -> item.getName().equalsIgnoreCase(killed.getType().name())).findFirst();
                optional.ifPresent(activityDTO -> ActivityBossbarManager.showBossBar(killer, activityDTO, ActivityBossbarManager.Type.PVE));
            }
        }
    }

}