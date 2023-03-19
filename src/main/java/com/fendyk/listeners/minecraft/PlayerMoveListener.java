package com.fendyk.listeners.minecraft;

import com.fendyk.Main;
import com.fendyk.managers.ChunkManager;
import com.fendyk.utilities.ChunkUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class PlayerMoveListener implements Listener {

    Main server;
    public PlayerMoveListener(Main server) {
        this.server = server;
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (server.getFrozenPlayers().contains(uuid)) {
            e.setCancelled(true);
            return;
        }

        Chunk currentChunk = player.getChunk();
        Chunk recentChunk = ChunkManager.getCurrentPlayerLocationChunks().getOrDefault(player.getUniqueId(), currentChunk);

        // If not the same
        if(!ChunkUtils.isSameChunk(currentChunk, recentChunk)) {
            ChunkManager.getCurrentPlayerLocationChunks().put(uuid, currentChunk);


            // Do stuff
        }
    }

}
