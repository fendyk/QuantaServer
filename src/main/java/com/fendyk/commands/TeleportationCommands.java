package com.fendyk.commands;

import com.fendyk.DTOs.LocationDTO;
import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.Main;
import com.fendyk.managers.ConfirmCommandManager;
import com.fendyk.utilities.PayableCommand;
import com.fendyk.utilities.RankConfiguration;
import com.fendyk.utilities.extentions.LuckPermsExtension;
import de.leonhard.storage.util.Valid;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import net.luckperms.api.model.user.User;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TeleportationCommands {
    Main main = Main.getInstance();

    public TeleportationCommands() {
        new CommandAPICommand("spawn")
                .executesPlayer((player, args) -> {

                    ValidateCommand validateCommand = new ValidateCommand.Builder(player)
                            .checkPrimaryGroup()
                            .checkRankConfiguration()
                            .checkMinecraftUserDTO()
                            .build();

                    if(!validateCommand.passed()) {
                        player.sendMessage("We could not pass the validation of the command.");
                        return;
                    }

                    ValidateCommand.Builder builder = validateCommand.getBuilder();
                    Location playerLocation = player.getLocation();
                    Location spawnLocation = main.getServerConfig().getSpawnLocation();

                    // Calculate the distance in blocks between both locations
                    double basePrice = main.getPricesConfig().getTeleportCommandPrice(); // example base price
                    double distance = playerLocation.getWorld().equals(spawnLocation.getWorld()) ? playerLocation.distance(spawnLocation) : 2500;
                    double price = Math.log(distance + 1) * basePrice;
                    double discountPercentage = builder.getRankConfiguration().getDiscountPercentage();

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
        new CommandAPICommand("back")
                .executesPlayer((player, args) -> {

                    ValidateCommand validateCommand = new ValidateCommand.Builder(player)
                            .checkPrimaryGroup()
                            .checkRankConfiguration()
                            .checkMinecraftUserDTO()
                            .checkLastLocation()
                            .build();

                    if(validateCommand.passed()) {
                        ValidateCommand.Builder builder = validateCommand.getBuilder();
                        Location playerLocation = player.getLocation();
                        Location targetLocation = LocationDTO.toLocation(builder.getMinecraftUserDTO().lastLocation);

                        // Calculate the distance in blocks between both locations
                        double basePrice = main.getPricesConfig().getTeleportCommandPrice(); // example base price
                        double distance = playerLocation.distance(targetLocation);
                        double price = Math.log(distance + 1) * basePrice * 2; // * 2 because it's not cheap
                        double discountPercentage = builder.getRankConfiguration().getDiscountPercentage();

                        if (!ConfirmCommandManager.isConfirmed(player)) {
                            ConfirmCommandManager.requestCommandConfirmation(player,
                                    new PayableCommand(
                                            "/back",
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

                        // Set the last location to the existing one before teeporting
                        boolean isUpdated = main.getApi().getMinecraftUserAPI().updateLastLocation(player, playerLocation);

                        if(!isUpdated) {
                            player.sendMessage(ChatColor.RED + "Could not update your last location");
                            return;
                        }

                        player.teleport(targetLocation);
                        player.sendMessage(ChatColor.GREEN + "You've been teleported to your latest .");
                    }

                })
                .register();
        new CommandAPICommand("tpr")
                .withArguments(new StringArgument("name"))
                .executesPlayer((player, args) -> {
                    String name = (String) args[0];


                    ValidateCommand validateCommand = new ValidateCommand.Builder(player)
                            .checkPrimaryGroup()
                            .checkRankConfiguration()
                            .build();

                    if(validateCommand.passed()) {
                        ValidateCommand.Builder builder = validateCommand.getBuilder();
                        String rankName = builder.getPrimaryGroup();

                        RankConfiguration configuration = builder.getRankConfiguration();

                        Location playerLocation = player.getLocation();
                        Location targetLocation = main.getServerConfig().getSpawnLocation(); // TODO:

                        // Calculate the distance in blocks between both locations
                        double basePrice = main.getPricesConfig().getTeleportCommandPrice(); // example base price
                        double distance = playerLocation.distance(targetLocation);
                        double price = Math.log(distance + 1) * basePrice * 2;
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

                        player.teleport(targetLocation);
                        player.sendMessage(ChatColor.GREEN + "You've been teleported to your latest .");

                    }

                })
                .withSubcommand(new CommandAPICommand("accept")
                        .withArguments(new StringArgument("name"))
                        .executesPlayer((player, args) -> {
                            String name = (String) args[0];

                            String rankName = LuckPermsExtension.getHighestGroup(player);
                            RankConfiguration configuration = main.getRanksConfig().getRankConfiguration(rankName);
                        })
                )
                .register();

    }

}
