package com.fendyk.expansions;

import com.fendyk.DTOs.LandDTO;
import com.fendyk.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LandExpansion extends PlaceholderExpansion {

    Main main = Main.getInstance();

    @Override
    public @NotNull String getIdentifier() {
        return "quantum_land";
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
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if(params.equalsIgnoreCase("current_land_name")) {
            LandDTO landDTO = main.getApi().getLandAPI().get(player.getUniqueId());
            return landDTO != null ? landDTO.getName() : "Not claimed";
        }
        return null;
    }
}
