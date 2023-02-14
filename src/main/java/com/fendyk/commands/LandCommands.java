package com.fendyk.commands;

import com.fendyk.API;
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

                            JsonElement eLand = api.getLandAPI().create(player.getUniqueId(), name, chunk);
                            if(eLand == null) {
                                player.sendMessage("Error when trying to find the land");
                                return;
                            }
                            else if(eLand.isJsonNull()) {
                                player.sendMessage("Land could not be found.");
                                return;
                            }

                            JsonObject jLand = eLand.getAsJsonObject();

                            player.sendMessage("Your land has been created");
                            player.sendMessage("Name: " + jLand.get("name").getAsString());
                        })
                )
                .withSubcommand(new CommandAPICommand("claim")
                        //.withRequirement(sender -> api.getLandAPI()( ((Player) sender).getUniqueId() ) != null)
                        .executes((sender, args) -> {
                            Player player = (Player) sender;
                            String name = (String) args[0];
                            Chunk chunk = player.getChunk();

                            JsonElement eLand = api.getLandAPI().get(player.getUniqueId());
                            if(eLand == null) {
                                player.sendMessage("Error when trying to find the land");
                                return;
                            }
                            else if(eLand.isJsonNull()) {
                                player.sendMessage("Land could not be found.");
                                return;
                            }

                            JsonObject JLand = eLand.getAsJsonObject();

                            boolean isClaimed = api.getChunkAPI().claim(chunk,JLand.get("id").getAsString());
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

                                            JsonElement eChunk = api.getChunkAPI().create(chunk, isNonClaimable);
                                            if(eChunk == null) {
                                                player.sendMessage("Error when trying to create a chunk");
                                                return;
                                            }
                                            else if(eChunk.isJsonNull()) {
                                                player.sendMessage("Chunk could not be found");
                                                return;
                                            }
                                            else if(eChunk.getAsJsonObject().isEmpty()) {
                                                player.sendMessage("Chunk is probably already generated");
                                                return;
                                            }


                                            player.sendMessage("Chunk has been generated");
                                        })
                                )
        )
                .register();
    }

}
