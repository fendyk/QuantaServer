package com.fendyk.expansions;

import com.fendyk.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

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
        if(params.equalsIgnoreCase("eco_balance")) {
            if(player.isOnline() && player.getPlayer() != null) {
                Float amount = main.getApi().getMinecraftUserAPI().getCached(player.getPlayer()).getQuanta();
                return amount != null ? String.format("%.2f", amount) : "undefined";
            }
        }

        return null;
    }
}
