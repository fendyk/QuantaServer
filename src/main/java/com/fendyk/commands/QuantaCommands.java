package com.fendyk.commands;

import com.fendyk.API;
import com.fendyk.Main;
import com.sk89q.minecraft.util.commands.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import java.math.BigDecimal;

@Command(aliases = {"balance", "quanta", "bal", "money"}, desc = "Shows your current balance in-game")
public class QuantaCommands {
    Main main = Main.getInstance();

    public QuantaCommands() {
        new CommandAPICommand("balance")
                .withAliases("bal", "quanta", "money")
                .executes((sender, args) -> {
                    Player player = (Player) sender;
                    BigDecimal amount = main.getApi().getMinecraftUserAPI().getPlayerBalance(player.getUniqueId());
                    player.sendMessage("Your balance is: " + amount);
                })
                .withSubcommand(new CommandAPICommand("deposit")
                        .withAliases("add")
                        .withArguments(new PlayerArgument("player"))
                        .withArguments(new DoubleArgument("amount"))
                        .withRequirement(ServerOperator::isOp)
                        .withPermission(CommandPermission.OP)
                        .executes((sender, args) -> {
                            Player player = (Player) args[0];
                            Double amount = (Double) args[1];
                            boolean success = main.getApi().getMinecraftUserAPI().depositBalance(player, new BigDecimal(amount));
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
                        .withRequirement(ServerOperator::isOp)
                        .withPermission(CommandPermission.OP)
                        .executes((sender, args) -> {
                            Player player = (Player) args[0];
                            Double amount = (Double) args[1];
                            boolean success = main.getApi().getMinecraftUserAPI().withDrawBalance(player, new BigDecimal(amount));
                            if(!success) {
                                player.sendMessage("Something went wrong when withdrawing. Please try again.");
                                return;
                            }
                            player.sendMessage("Your have withdrawn " + amount + " from " + player.getName());
                        })
                )
                .register();
    }

}
