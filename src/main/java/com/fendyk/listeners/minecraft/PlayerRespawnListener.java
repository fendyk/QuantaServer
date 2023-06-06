package com.fendyk.listeners.minecraft;

import com.fendyk.DTOs.LandDTO;
import com.fendyk.DTOs.LocationDTO;
import com.fendyk.DTOs.TaggedLocationDTO;
import com.fendyk.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Optional;

public class PlayerRespawnListener implements Listener {

    Main main = Main.getInstance();

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        LandDTO landDTO = main.getApi().getLandAPI().get(player.getUniqueId());

        if(landDTO == null) return;

        Optional<TaggedLocationDTO> oLoc = landDTO.getHomes().stream()
                .filter(item -> item.getName().equalsIgnoreCase("spawn"))
                .findAny();

        if(oLoc.isEmpty()) return;

        TaggedLocationDTO taggedLoc = oLoc.get();
        Location loc = LocationDTO.toLocation(taggedLoc.getLocation());

        event.setRespawnLocation(loc);

        // TODO: Add message after the teleport.

    }
}
