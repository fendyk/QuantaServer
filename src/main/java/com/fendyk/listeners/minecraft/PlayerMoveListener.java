package com.fendyk.listeners.minecraft;

import com.fendyk.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class PlayerMoveListener implements Listener {

    Main main = Main.getInstance();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (main.getFrozenPlayers().contains(uuid)) {
            e.setCancelled(true);
            return;
        }

        /*
        Chunk currentChunk = player.getChunk();
        Chunk recentChunk = ChunkManager.getCurrentPlayerLocationChunks().getOrDefault(player.getUniqueId(), currentChunk);

        // If not the same
        if(!ChunkUtils.isSameChunk(currentChunk, recentChunk)) {
            ChunkManager.getCurrentPlayerLocationChunks().put(uuid, currentChunk);


            // Do stuff
        }

         */
    }

}
