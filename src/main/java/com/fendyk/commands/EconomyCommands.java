package com.fendyk.commands;

import com.fendyk.ChannelAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EconomyCommands {

    public EconomyCommands(ChannelAPI channelAPI) {
        new CommandAPICommand("balance")
                .withArguments(new GreedyStringArgument("message"))
                .withAliases("bal", "quanta", "money")
                .executes((sender, args) -> {
                    String message = (String) args[0];
                    Player player = (Player) sender;

                    channelAPI.request.playerBalance(player.getUniqueId());

                })
                .register();
    }

}
