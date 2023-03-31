package com.fendyk.commands;

import com.fendyk.Main;
import com.fendyk.managers.ConfirmCommandManager;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.math.BigDecimal;

public class SpawnCommands {
    Main main = Main.getInstance();

    public SpawnCommands() {
        new CommandAPICommand("spawn")
                .executesPlayer((player, args) -> {

                    Location playerLocation = player.getLocation();
                    Location spawnLocation = main.getServerConfig().getSpawnLocation();

                    // Calculate the distance in blocks between both locations
                    double basePrice = main.getPricesConfig().getSpawnCommandPrice(); // example base price
                    double distance = playerLocation.distance(spawnLocation);
                    double price = Math.log(distance + 1) * basePrice;

                    if(!ConfirmCommandManager.isConfirmed(player)) {
                        ConfirmCommandManager.requestCommandConfirmation(player, "spawn", price, 30L);
                        return;
                    }

                    boolean isWithdrawn = main.getApi().getMinecraftUserAPI().withDrawBalance(player, new BigDecimal(price));

                    if(!isWithdrawn) {
                        player.sendMessage(ChatColor.RED + "Could not withdraw money.");
                        return;
                    }

                    player.teleport(spawnLocation);
                    player.sendMessage(ChatColor.GREEN + "You've been teleported to the spawn.");

                })
                .register();

    }

}
