package com.fendyk.commands;

import com.fendyk.Main;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

public class GeneralCommands {
    Main main = Main.getInstance();
    public GeneralCommands() {
        new CommandAPICommand("speed")
                .withRequirement(ServerOperator::isOp)
                .withArguments(new FloatArgument("amount"))
                .executesPlayer((player, args) -> {
                    player.setFlySpeed((Float) args[0] / 10);
                })
                .register();

        new CommandAPICommand("gm")
                .withRequirement(ServerOperator::isOp)
                .withArguments(new IntegerArgument("number"))
                .executesPlayer((player, args) -> {
                    switch ((Integer) args[0]) {
                        case 0 -> player.setGameMode(GameMode.SURVIVAL);
                        case 1 -> player.setGameMode(GameMode.CREATIVE);
                        case 2 -> player.setGameMode(GameMode.ADVENTURE);
                        case 3 -> player.setGameMode(GameMode.SPECTATOR);
                    }
                })
                .register();

        new CommandAPICommand("tp")
                .withRequirement(ServerOperator::isOp)
                .withArguments(new PlayerArgument("player"))
                .executesPlayer((player, args) -> {
                    player.teleport((Player)args[0]);
                })
                .register();

        new CommandAPICommand("tp")
                .withRequirement(ServerOperator::isOp)
                .withArguments(new LocationArgument("player"))
                .executesPlayer((player, args) -> {
                    player.teleport((Player)args[0]);
                })
                .register();
    }

}
