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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ActivityBossBarManager {

    static Main main = Main.instance;
    static HashMap<UUID, HashMap<Type, BossBar>> bossBars = new HashMap<>();
    static HashMap<UUID, HashMap<Type, Long>> bossBarsExpiresInSeconds = new HashMap<>();

    public static void watch() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
            for (Map.Entry<UUID, HashMap<Type, Long>> entry : bossBarsExpiresInSeconds.entrySet()) {
                UUID uuid = entry.getKey();
                HashMap<Type, Long> expires = entry.getValue();
                expires.replaceAll((k, v) -> v - 4);
                expires.entrySet().removeIf(entry2 -> {
                    Type type = entry2.getKey();
                    Long seconds = entry2.getValue();

                    if (seconds <= 0) { // Hide the bossbar
                        main.adventure().player(uuid).hideBossBar(bossBars.get(uuid).get(type));
                        return true;
                    }
                    return false;
                });
            }

        }, 0, 80L);
    }

    public static void showBossBar(Player player, ActivityDTO activityDTO, Type type) {
        UUID uuid = player.getUniqueId();
        Audience audience = main.adventure().player(uuid);

        final float[] percentAndThreshold = getPercentAndThresholdFromActivity(activityDTO);
        final float percent = Math.min(percentAndThreshold[0], 1);
        final float threshold = percentAndThreshold[1];
        final BossBar.Color color = getColorByType(type);
        final Component name = Component.text(activityDTO.name + " Progression ( " + Math.round(activityDTO.quantity) + " / " + Math.round(threshold) + " )");

        bossBars.entrySet().stream().filter(item -> item.getKey().equals(player.getUniqueId()))
                .findFirst()
                .ifPresentOrElse((item) -> {
                    item.getValue().entrySet().stream().filter(item2 -> item2.getKey() == type)
                            .findFirst()
                            .ifPresentOrElse((item2) -> {
                                // Update stuff here (and show it to audience)
                                BossBar bossBar = item2.getValue();
                                bossBar.progress(percent);
                                bossBar.name(name);
                                audience.showBossBar(bossBar);
                            }, () -> {
                                // Otherwise we just put the new bossbar.
                                final BossBar bossBar = BossBar.bossBar(name, percent, color, BossBar.Overlay.PROGRESS);
                                item.getValue().put(type, bossBar);
                                audience.showBossBar(bossBar);
                            });
                }, () -> {
                    HashMap<Type, BossBar> playerBossBars = new HashMap<>();
                    final BossBar bossBar = BossBar.bossBar(name, percent, color, BossBar.Overlay.PROGRESS);
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
                                item2.setValue(10L);
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

    public static float[] getPercentAndThresholdFromActivity(ActivityDTO activityDTO) {
        float[] result = new float[2];
        final double quantity = activityDTO.quantity;
        final String name = activityDTO.name;
        EarningsConfig earningsConfig = main.earningsConfig;

        if (name.equalsIgnoreCase("SECONDS")) {
            float threshold = (float) earningsConfig.getTimeThreshold();
            result[0] = (float) (quantity / threshold);
            result[1] = threshold;
        }

        if (UUIDChecker.isValidUuid(name)) {
            float threshold = (float) earningsConfig.getPvpThreshold();
            result[0] = (float) (quantity / threshold);
            result[1] = threshold;
        }

        Optional<Map.Entry<EntityType, Double>> optionalEntityThreshold = earningsConfig.getEntityThreshold().entrySet().stream().filter(item -> item.getKey().toString().equalsIgnoreCase(name)).findFirst();
        if (optionalEntityThreshold.isPresent()) {
            float threshold = optionalEntityThreshold.get().getValue().floatValue();
            result[0] = (float) (quantity / threshold);
            result[1] = threshold;
        }

        Optional<Map.Entry<Material, Double>> optionalMaterialThreshold = earningsConfig.getMaterialThreshold().entrySet().stream().filter(item -> item.getKey().toString().equalsIgnoreCase(name)).findFirst();
        if (optionalMaterialThreshold.isPresent()) {
            float threshold = optionalMaterialThreshold.get().getValue().floatValue();
            result[0] = (float) (quantity / threshold);
            result[1] = threshold;
        }

        return result;
    }

    public static HashMap<UUID, HashMap<Type, BossBar>> getBossBars() {
        return bossBars;
    }

    public static HashMap<UUID, HashMap<Type, Long>> getBossBarsExpiresInSeconds() {
        return bossBarsExpiresInSeconds;
    }

    public enum Type {
        MINING, PVE, PVP, TIME
    }
}
