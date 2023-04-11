package com.fendyk.expansions;

import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.ActivityDTO;
import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

public class QuantaExpansion extends PlaceholderExpansion {
    Main main = Main.getInstance();
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

        if(params.equalsIgnoreCase("eco_balance")) {
            if(player.isOnline() && player.getPlayer() != null) {
                MinecraftUserDTO minecraftUserDTO = main.getApi().getMinecraftUserAPI().getCached(uuid);
                return minecraftUserDTO != null ? String.format("%.2f", minecraftUserDTO.getQuanta()) : "0";
            }
        }

        if(params.equalsIgnoreCase("activities_mining_daily_earned")) {
            ActivitiesDTO activitiesDTO = main.getApi().getActivitiesAPI().getCached(player.getUniqueId());
            return activitiesDTO != null ?
                    String.format("%.2f", activitiesDTO.getMining().stream().mapToDouble(ActivityDTO::getEarnings).sum())
                    : "0";
        }

        if(params.equalsIgnoreCase("activities_pve_daily_earned")) {
            ActivitiesDTO activitiesDTO = main.getApi().getActivitiesAPI().getCached(player.getUniqueId());
            return activitiesDTO != null ?
                    String.format("%.2f", activitiesDTO.getPve().stream().mapToDouble(ActivityDTO::getEarnings).sum())
                    : "0";
        }

        if(params.equalsIgnoreCase("activities_mining_daily_quantity")) {
            ActivitiesDTO activitiesDTO = main.getApi().getActivitiesAPI().getCached(player.getUniqueId());
            return activitiesDTO != null ?
                    String.format("%.2f", activitiesDTO.getMining().stream().mapToDouble(ActivityDTO::getQuantity).sum())
                    : "0";
        }

        if(params.equalsIgnoreCase("activities_pve_daily_quantity")) {
            ActivitiesDTO activitiesDTO = main.getApi().getActivitiesAPI().getCached(player.getUniqueId());
            return activitiesDTO != null ?
                    String.format("%.2f", activitiesDTO.getPve().stream().mapToDouble(ActivityDTO::getQuantity).sum())
                    : "0";
        }

        return null;
    }
}
