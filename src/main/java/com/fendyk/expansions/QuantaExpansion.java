package com.fendyk.expansions;

import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.ActivityDTO;
import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class QuantaExpansion extends PlaceholderExpansion {
    Main main = Main.instance;

    @Override
    public @NotNull String getIdentifier() {
        return "quantum";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Raul Fernandez";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public @Nullable String onRequest(@NotNull OfflinePlayer player, @NotNull String params) {
        UUID uuid = player.getUniqueId();

        if (params.equalsIgnoreCase("eco_balance")) {
            if (player.isOnline() && player.getPlayer() != null) {
                MinecraftUserDTO minecraftUserDTO = main.api.minecraftUserAPI.getCached(uuid);
                return minecraftUserDTO != null ? String.format("%.2f", minecraftUserDTO.quanta) : "0";
            }
        }

        if (params.equalsIgnoreCase("activities_mining_daily_earned")) {
            ActivitiesDTO activitiesDTO = main.api.activitiesAPI.getCached(player.getUniqueId());
            return activitiesDTO != null ?
                    String.format("%.2f", activitiesDTO.mining.stream().mapToDouble(ActivityDTO::getEarnings).sum())
                    : "0";
        }

        if (params.equalsIgnoreCase("activities_pve_daily_earned")) {
            ActivitiesDTO activitiesDTO = main.api.activitiesAPI.getCached(player.getUniqueId());
            return activitiesDTO != null ?
                    String.format("%.2f", activitiesDTO.pve.stream().mapToDouble(ActivityDTO::getEarnings).sum())
                    : "0";
        }

        if (params.equalsIgnoreCase("activities_mining_daily_quantity")) {
            ActivitiesDTO activitiesDTO = main.api.activitiesAPI.getCached(player.getUniqueId());
            return activitiesDTO != null ?
                    String.format("%.2f", activitiesDTO.mining.stream().mapToDouble(ActivityDTO::getQuantity).sum())
                    : "0";
        }

        if (params.equalsIgnoreCase("activities_pve_daily_quantity")) {
            ActivitiesDTO activitiesDTO = main.api.activitiesAPI.getCached(player.getUniqueId());
            return activitiesDTO != null ?
                    String.format("%.2f", activitiesDTO.pve.stream().mapToDouble(ActivityDTO::getQuantity).sum())
                    : "0";
        }

        return null;
    }
}
