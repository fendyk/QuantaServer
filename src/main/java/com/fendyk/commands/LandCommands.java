package com.fendyk.commands;

import com.fendyk.API;
import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.clients.apis.MinecraftUserAPI;
import com.fendyk.utilities.Vector2;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class LandCommands {

    public LandCommands(API api) {
        new CommandAPICommand("land")
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
                                Bukkit.getLogger().info(Arrays.toString(e.getStackTrace()));
                                player.sendMessage(e.getMessage());
                            }
                        })

                )
                .withSubcommand(new CommandAPICommand("chunk")
                        .withSubcommand(new CommandAPICommand("generate")
                                //.withRequirement(sender -> api.getLandAPI()( ((Player) sender).getUniqueId() ) != null)
                                .withArguments(new BooleanArgument("isClaimable"))
                                .executes((sender, args) -> {
                                    Player player = (Player) sender;
                                    boolean isClaimable = (boolean) args[0];
                                    Chunk chunk = player.getChunk();

                                    ChunkDTO chunkDTO = api.getChunkAPI().create(chunk, isClaimable);
                                    if(chunkDTO == null) {
                                        player.sendMessage("Error when trying to create a chunk");
                                        return;
                                    }

                                    player.sendMessage("Chunk has been generated");
                                })
                        )
                        .withSubcommand(new CommandAPICommand("claim")
                                //.withRequirement(sender -> api.getLandAPI()( ((Player) sender).getUniqueId() ) != null)
                                .executes((sender, args) -> {
                                    Player player = (Player) sender;
                                    Chunk chunk = player.getChunk();

                                    ChunkDTO chunkDTO = api.getChunkAPI().getRedis().get(new Vector2(chunk.getX(), chunk.getZ()));

                                    if(chunkDTO == null) {
                                        chunkDTO = api.getChunkAPI().create(chunk, true);

                                        if(chunkDTO == null) {
                                            player.sendMessage("Chunk could not be found.");
                                            return;
                                        }
                                    }

                                    String chunkLandId = chunkDTO.getLandId();
                                    if(chunkLandId != null) {
                                        player.sendMessage("This chunk has already been claimed by someone else");
                                        return;
                                    }

                                    MinecraftUserDTO minecraftUserDTO = api.getMinecraftUserAPI().getRedis().get(player.getUniqueId());
                                    if(minecraftUserDTO == null) {
                                        player.sendMessage("Something went wrong when fetching your data.");
                                        return;
                                    }

                                    LandDTO landDTO = api.getLandAPI().getRedis().get(minecraftUserDTO.getId());
                                    if(landDTO == null) {
                                        player.sendMessage("Error when trying to find the land");
                                        return;
                                    }

                                    String landId = landDTO.getId();
                                    if(landId == null) {
                                        player.sendMessage("You currently dont have a land. To create one, type /land create <name>");
                                        return;
                                    }

                                    if(!chunkDTO.isClaimable()) {
                                        player.sendMessage("This chunk is consired 'not claimable'");
                                        return;
                                    }

                                    boolean isClaimed = api.getChunkAPI().claim(chunk,landDTO.getId());
                                    if(!isClaimed) {
                                        player.sendMessage("Could not claim chunk .");
                                        return;
                                    }

                                    player.sendMessage("Chunk has been claimed");
                                })
                        )
                        .withSubcommand(new CommandAPICommand("info")
                                //.withRequirement(sender -> api.getLandAPI()( ((Player) sender).getUniqueId() ) != null)
                                .executes((sender, args) -> {
                                    Player player = (Player) sender;
                                    Chunk chunk = player.getChunk();

                                    ChunkDTO chunkDTO = api.getChunkAPI().getRedis().get(new Vector2(chunk.getX(), chunk.getZ()));

                                    if(chunkDTO == null) {
                                        player.sendMessage("This chunk has not been claimed yet.");
                                        return;
                                    }

                                    LandDTO landDTO = api.getLandAPI().getRedis().get(chunkDTO.getLandId());

                                    if(landDTO == null) {
                                        player.sendMessage("Error when trying to find the land");
                                        return;
                                    }

                                    MinecraftUserDTO minecraftUserDTO = api.getMinecraftUserAPI().get(UUID.fromString(landDTO.getOwnerId()), false);

                                    if(minecraftUserDTO == null) {
                                        player.sendMessage("Error when trying to find owner's land");
                                        return;
                                    }

                                    player.sendMessage("You're currently standing at:");
                                    player.sendMessage("Chunk: " + chunkDTO.getX() + "/" + chunkDTO.getZ());
                                    player.sendMessage("Land:" + landDTO.getName());
                                    player.sendMessage("Owned by player: " + Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(minecraftUserDTO.getId()))).getName());
                                })
                        )
        )
                .register();
    }

}
