package com.fendyk.commands;

import com.fendyk.Main;
import com.fendyk.managers.ConfirmCommandManager;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.math.BigDecimal;

public class SpawnCommands {
    Main main = Main.getInstance();

    public SpawnCommands() {
        new CommandAPICommand("spawn")
                .executesPlayer((player, args) -> {

                    Location playerLocation = player.getLocation();
                    Location spawnLocation = new Location(Bukkit.getWorld("world"), 0,0,0);

                    // Calculate the distance in blocks between both locations
                    double basePrice = 1.0; // example base price
                    double distance = playerLocation.distance(spawnLocation);
                    double price = Math.log(distance + 1) * basePrice;

                    if(!ConfirmCommandManager.isConfirmed(player)) {
                        ConfirmCommandManager.requestCommandConfirmation(player, "spawn", price, 30L);
                    }

                    boolean isWithdrawn = main.getApi().getMinecraftUserAPI().withDrawBalance(player, new BigDecimal(price));

                    if(!isWithdrawn) {
                        player.sendMessage("Could not withdraw money.");
                    }

                    player.teleport(spawnLocation);


                })
                .register();

    }

}
