package com.fendyk.listeners.minecraft;

import com.fendyk.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {

    Main main = Main.getInstance();

    @EventHandler
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
        // TODO: Make sure we track the latest TP to be used for /back
    }

}
