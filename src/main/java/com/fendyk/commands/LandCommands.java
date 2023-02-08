package com.fendyk.commands;

import com.fendyk.API;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class LandCommands {

    public LandCommands(API api) {
        new CommandAPICommand("land")
                .executes((sender, args) -> {

                })
                .withSubcommand(new CommandAPICommand("create")
                        .withArguments(new StringArgument("name"))
                        .executes((sender, args) -> {
                            Player player = (Player) sender;
                            String name = (String) args[0];
                            Chunk chunk = player.getChunk();

                            api.createLand(player.getUniqueId(), name, chunk);
                        })
                )
                .withSubcommand(new CommandAPICommand("claim")
                        .withRequirement(sender -> api.getLand( ((Player) sender).getUniqueId() ) != null)
                        .executes((sender, args) -> {
                            Player player = (Player) sender;
                            String name = (String) args[0];
                            Chunk chunk = player.getChunk();

                            api.claimChunkForLand(player.getUniqueId(), chunk);
                        })
                )
                .register();
    }

}
