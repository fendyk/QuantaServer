package com.fendyk.commands;

import com.fendyk.RedisAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;

import java.awt.*;
import java.math.BigDecimal;

public class EconomyCommands {

    public EconomyCommands(RedisAPI redisAPI) {
        new CommandAPICommand("balance")
                .withAliases("bal", "quanta", "money")
                .executes((sender, args) -> {
                    Player player = (Player) sender;
                    BigDecimal amount = redisAPI.getPlayerBalance(player.getUniqueId());
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
                            redisAPI.depositBalance(player.getUniqueId(), new BigDecimal(amount));
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
                            redisAPI.withdrawBalance(player.getUniqueId(), new BigDecimal(amount));
                            player.sendMessage("Your have withdrawn " + amount + " from " + player.getName());
                        })
                )
                .register();
    }

}
