package com.fendyk.commands;

import com.fendyk.Main;
import com.fendyk.managers.ConfirmCommandManager;
import com.fendyk.utilities.PayableCommand;
import com.fendyk.utilities.RankConfiguration;
import com.fendyk.utilities.extentions.LuckPermsExtension;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.math.BigDecimal;
import java.util.ArrayList;

public class SpawnCommands {
    Main main = Main.getInstance();

    public SpawnCommands() {
        new CommandAPICommand("spawn")
                .executesPlayer((player, args) -> {

                    String rankName = LuckPermsExtension.getHighestGroup(player);
                    RankConfiguration configuration = main.getRanksConfig().getRankConfiguration(rankName);

                    if(configuration == null) {
                        player.sendMessage("Something went wrong when accessing the configuration of 'ranks'. ");
                        return;
                    }

                    Location playerLocation = player.getLocation();
                    Location spawnLocation = main.getServerConfig().getSpawnLocation();

                    // Calculate the distance in blocks between both locations
                    double basePrice = main.getPricesConfig().getSpawnCommandPrice(); // example base price
                    double distance = playerLocation.distance(spawnLocation);
                    double price = Math.log(distance + 1) * basePrice;
                    double discountPercentage = configuration.getDiscountPercentage();

                    // TODO Make the command work
                    if (!ConfirmCommandManager.isConfirmed(player)) {
                        ConfirmCommandManager.requestCommandConfirmation(player,
                                new PayableCommand(
                                        "/spawn",
                                        new ArrayList<>(),
                                        price,
                                        30L,
                                        discountPercentage
                                )
                        );
                        return;
                    }

                    boolean isWithdrawn = main.getApi().getMinecraftUserAPI().withDrawBalance(player, new BigDecimal(price));

                    if (!isWithdrawn) {
                        player.sendMessage(ChatColor.RED + "Could not withdraw money.");
                        return;
                    }

                    player.teleport(spawnLocation);
                    player.sendMessage(ChatColor.GREEN + "You've been teleported to the spawn.");

                })
                .register();

    }

}
