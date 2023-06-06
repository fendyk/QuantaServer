package com.fendyk.listeners.minecraft;

import com.fendyk.DTOs.LandDTO;
import com.fendyk.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    Main main = Main.getInstance();

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        LandDTO landDTO = main.getApi().getLandAPI().get(player.getUniqueId());
        // TODO: If has land, respawn the player on it's home land
    }
}
