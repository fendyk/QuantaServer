package com.fendyk.listeners.minecraft;

import com.fendyk.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    Main server;
    public PlayerQuitListener(Main server) {
        this.server = server;
    }
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        server.getFrozenPlayers().remove(player.getUniqueId());
    }

}
