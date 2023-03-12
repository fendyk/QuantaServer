package com.fendyk.managers;

import com.fendyk.DTOs.ActivityDTO;
import com.fendyk.Main;
import com.fendyk.configs.EarningsConfig;
import com.fendyk.utilities.UUIDChecker;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class ActivityBossbarManager {

    public enum Type {
        MINING, PVE, PVP, TIME
    }

    static Main main = Main.getInstance();
    static HashMap<UUID, HashMap<Type, BossBar>> bossBars = new HashMap<>();
    static HashMap<UUID, HashMap<Type, Long>> bossBarsExpiresInSeconds = new HashMap<>();

    public static void watch() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
            for (Map.Entry<UUID, HashMap<Type, Long>> entry : bossBarsExpiresInSeconds.entrySet()) {
                UUID uuid = entry.getKey();
                HashMap<Type, Long> expires = entry.getValue();

                expires.replaceAll((k, v) -> v -1);

                expires.entrySet().removeIf(entry2 -> {
                    Type type = entry2.getKey();
                    Long seconds = entry2.getValue();
                    if(seconds <= 0) { // Hide the bossbar
                        main.adventure().player(uuid).hideBossBar(bossBars.get(uuid).get(type));
                        return true;
                    }
                    return false;
                });
            }

        }, 20L, 100L);
    }

    public static void showBossBar(Player player, ActivityDTO activityDTO, Type type) {
        UUID uuid = player.getUniqueId();
        Audience audience = main.adventure().player(uuid);

        final Component name = Component.text("Activity BossBar");
        final BossBar bossBar = BossBar.bossBar(name, getDecimalPercentFromActivity(activityDTO), getColorByType(type), BossBar.Overlay.PROGRESS);

        bossBars.entrySet().stream().filter(item -> item.getKey().equals(player.getUniqueId()))
                .findFirst()
                .ifPresentOrElse((item) -> {
                    item.getValue().entrySet().stream().filter(item2 -> item2.getKey() == type)
                            .findFirst()
                            .ifPresentOrElse((item2) -> {
                                // Update stuff here
                                item2.getValue().progress(getDecimalPercentFromActivity(activityDTO));
                            }, () -> {
                                // Otherwise we just put the new bossbar.
                                item.getValue().put(type, bossBar);
                                audience.showBossBar(bossBar);
                            });
                }, () -> {
                    HashMap<Type, BossBar> playerBossBars = new HashMap<>();
                    playerBossBars.put(type, bossBar);
                    bossBars.put(uuid, playerBossBars);
                    audience.showBossBar(bossBar);
                });

        bossBarsExpiresInSeconds.entrySet().stream().filter(item -> item.getKey().equals(player.getUniqueId()))
                .findFirst()
                .ifPresentOrElse((item) -> {
                    item.getValue().entrySet().stream().filter(item2 -> item2.getKey() == type)
                            .findFirst()
                            .ifPresentOrElse((item2) -> {
                                // Update stuff here
                                item2.setValue(item2.getValue() + 10L);
                            }, () -> {
                                // Otherwise we just put the new bossbar.
                                item.getValue().put(type, 10L);
                            });
                }, () -> {
                    HashMap<Type, Long> playerBossBarsExpiresInSeconds = new HashMap<>();
                    playerBossBarsExpiresInSeconds.put(type, 10L);
                    bossBarsExpiresInSeconds.put(uuid, playerBossBarsExpiresInSeconds);
                });
    }

    public static BossBar.Color getColorByType(Type type) {
        return switch (type) {
            case MINING -> BossBar.Color.BLUE;
            case PVE -> BossBar.Color.PINK;
            case PVP -> BossBar.Color.RED;
            case TIME -> BossBar.Color.YELLOW;
        };
    }

    public static float getDecimalPercentFromActivity(ActivityDTO activityDTO) {
        final double quantity = activityDTO.getQuantity();
        final String name = activityDTO.getName();
        EarningsConfig earningsConfig = main.getEarningsConfig();

        if(name.equalsIgnoreCase("SECONDS")) {
            return (float) (quantity / earningsConfig.getTimeThreshold());
        }

        if(UUIDChecker.isValidUuid(name)) {
            return (float) (quantity / earningsConfig.getPvpThreshold());
        }

        Optional<Map.Entry<EntityType, Double>> optionalEntityThreshold = earningsConfig.getEntityThreshold().entrySet().stream().filter(item -> item.getKey().toString().equalsIgnoreCase(name)).findFirst();
        if(optionalEntityThreshold.isPresent()) {
            return (float) (quantity / optionalEntityThreshold.get().getValue());
        }

        Optional<Map.Entry<Material, Double>> optionalMaterialThreshold = earningsConfig.getMaterialThreshold().entrySet().stream().filter(item -> item.getKey().toString().equalsIgnoreCase(name)).findFirst();
        return optionalMaterialThreshold.map(materialDoubleEntry -> (float) (quantity / materialDoubleEntry.getValue())).orElse(0F);
    }



}
