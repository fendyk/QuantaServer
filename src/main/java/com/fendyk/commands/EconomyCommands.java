package com.fendyk.commands;

import com.fendyk.API;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class EconomyCommands {

    public EconomyCommands(API api) {
        new CommandAPICommand("balance")
                .withAliases("bal", "quanta", "money")
                .executes((sender, args) -> {
                    Player player = (Player) sender;
                    BigDecimal amount = api.getMinecraftUserAPI().getPlayerBalance(player.getUniqueId());
                    player.sendMessage("Your balance is: " + amount);
                })
                .withSubcommand(new CommandAPICommand("deposit")
                        .withAliases("add")
                        .withArguments(new PlayerArgument("player"))
                        .withArguments(new DoubleArgument("amount"))
                        .withPermission(CommandPermission.OP)
                        .executes((sender, args) -> {
                            Player player = (Player) args[0];
                            Double amount = (Double) args[1];
                            boolean success = api.getMinecraftUserAPI().depositBalance(player.getUniqueId(), new BigDecimal(amount));
                            if(!success) {
                                player.sendMessage("Something went wrong when depositing. Please try again.");
                                return;
                            }
                            player.sendMessage("Your have deposited " + amount + " to " + player.getName());
                        })
                )
                .withSubcommand(new CommandAPICommand("withdraw")
                        .withAliases("remove")
                        .withArguments(new PlayerArgument("player"))
                        .withArguments(new DoubleArgument("amount"))
                        .withPermission(CommandPermission.OP)
                        .executes((sender, args) -> {
                            Player player = (Player) args[0];
                            Double amount = (Double) args[1];
                            boolean success =api.getMinecraftUserAPI().withDrawBalance(player.getUniqueId(), new BigDecimal(amount));
                            if(!success) {
                                player.sendMessage("Something went wrong when withdrawing. Please try again.");
                                return;
                            }
                            player.sendMessage("Your have withdrawn " + amount + " from " + player.getName());
                        })
                )
                .register();

        new CommandAPICommand("pay")
                .withArguments(new PlayerArgument("player"))
                .withArguments(new DoubleArgument("amount"))
                .executes((sender, args) -> {
                    Player fromPlayer = (Player) sender;
                    Player toPlayer = (Player) args[0];
                    Double amount = (Double) args[1];

                    BigDecimal balance = api.getMinecraftUserAPI().getPlayerBalance(fromPlayer.getUniqueId());

                    if(balance == null || balance.doubleValue() < amount) {
                        fromPlayer.sendMessage("You dont have enough money to send.");
                        return;
                    }
                    else if(balance.doubleValue() < 0) {
                        fromPlayer.sendMessage("Cannot send money without money, makes sense right.. right?");
                        return;
                    }

                    boolean isWithdrawn = api.getMinecraftUserAPI().withDrawBalance(fromPlayer.getUniqueId(), new BigDecimal(amount));
                    boolean isDeposited = api.getMinecraftUserAPI().depositBalance(toPlayer.getUniqueId(), new BigDecimal(amount));
                    if(!isWithdrawn || !isDeposited) {
                        fromPlayer.sendMessage("Looks like something went wrong. Maybe you dont have enough $Quanta? If you believe this is an error, contact support.");
                        return;
                    }
                    fromPlayer.sendMessage("Your have payed " + amount + " to " + toPlayer.getName());
                    toPlayer.sendMessage("Your have received " + amount + " from " + fromPlayer.getName());
                })
                .register();
    }

}
