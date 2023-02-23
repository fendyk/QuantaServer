package com.fendyk.commands;

import com.fendyk.API;
import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.LandDTO;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.BooleanArgument;
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
                            try {
                                LandDTO landDTO = api.getLandAPI().create(player.getUniqueId(), name, chunk);
                                player.sendMessage("Your land has been created");
                            } catch (Exception e) {
                                player.sendMessage(e.getMessage());
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("claim")
                        //.withRequirement(sender -> api.getLandAPI()( ((Player) sender).getUniqueId() ) != null)
                        .executes((sender, args) -> {
                            Player player = (Player) sender;
                            Chunk chunk = player.getChunk();

                            LandDTO landDTO = api.getLandAPI().get(player.getUniqueId());

                            if(landDTO == null) {
                                player.sendMessage("Error when trying to find the land");
                                return;
                            }

                            boolean isClaimed = api.getChunkAPI().claim(chunk,landDTO.getId());
                            if(!isClaimed) {
                                player.sendMessage("Could not find or claim chunk");
                                return;
                            }

                            player.sendMessage("Chunk has been claimed");
                        })
                )
                .withSubcommand(new CommandAPICommand("chunk")
                                .withSubcommand(new CommandAPICommand("generate")
                                        //.withRequirement(sender -> api.getLandAPI()( ((Player) sender).getUniqueId() ) != null)
                                        .withArguments(new BooleanArgument("isNonClaimable"))
                                        .executes((sender, args) -> {
                                            Player player = (Player) sender;
                                            boolean isNonClaimable = (boolean) args[0];
                                            Chunk chunk = player.getChunk();

                                            ChunkDTO chunkDTO = api.getChunkAPI().create(chunk, isNonClaimable);
                                            if(chunkDTO == null) {
                                                player.sendMessage("Error when trying to create a chunk");
                                                return;
                                            }

                                            player.sendMessage("Chunk has been generated");
                                        })
                                )
        )
                .register();
    }

}
