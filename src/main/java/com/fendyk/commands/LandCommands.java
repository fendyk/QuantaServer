package com.fendyk.commands;

import com.fendyk.API;
import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.Main;
import com.fendyk.clients.apis.MinecraftUserAPI;
import com.fendyk.managers.WorldguardSyncManager;
import com.fendyk.utilities.Vector2;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.color.DustData;
import xyz.xenondevs.particle.data.color.RegularColor;
import xyz.xenondevs.particle.task.TaskManager;

import java.util.*;

public class LandCommands {

    public LandCommands(Main server) {
        API api = server.getApi();
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
                                        WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(239, 68, 68, 15));
                                        player.sendMessage("This chunk has already been claimed by someone else");
                                        return;
                                    }

                                    MinecraftUserDTO minecraftUserDTO = api.getMinecraftUserAPI().getRedis().get(player.getUniqueId());
                                    if(minecraftUserDTO == null) {
                                        player.sendMessage("Something went wrong when fetching your data.");
                                        return;
                                    }

                                    LandDTO landDTO = api.getLandAPI().getRedis().get(player.getUniqueId().toString());
                                    if(landDTO == null) {
                                        player.sendMessage("You currently dont have a land. To create one, type /land create <name>");
                                        return;
                                    }

                                    if(!chunkDTO.isClaimable()) {
                                        WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(252, 211, 77, 15));
                                        player.sendMessage("This chunk is considered not claimable");
                                        return;
                                    }

                                    // Find out if there is a neighbour.
                                    List<Chunk> neighbours = WorldguardSyncManager.getNeighboringChunks(chunk);
                                    boolean hasNeighbour = false;
                                    for(Chunk neighbour : neighbours) {
                                        ChunkDTO neighbourChunkDTO = api.getChunkAPI().getRedis().get(new Vector2(neighbour.getX(), neighbour.getZ()));
                                        if(neighbourChunkDTO != null && neighbourChunkDTO.getLandId() != null && neighbourChunkDTO.getLandId().equals(landDTO.getId())) {
                                            hasNeighbour = true;
                                        }
                                    }

                                    if(!hasNeighbour) {
                                        player.sendMessage("You can only claim chunks that are neighbours of you current land.");
                                        return;
                                    }

                                    boolean isClaimed = api.getChunkAPI().claim(chunk,landDTO.getId());
                                    if(!isClaimed) {
                                        player.sendMessage("Could not claim chunk .");
                                        return;
                                    }

                                    WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(16, 185, 129, 15));
                                    player.sendMessage("Chunk has been claimed");
                                })
                        )
                        .withSubcommand(new CommandAPICommand("info")
                                //.withRequirement(sender -> api.getLandAPI()( ((Player) sender).getUniqueId() ) != null)
                                .executes((sender, args) -> {
                                    Player player = (Player) sender;
                                    Chunk chunk = player.getChunk();

                                    ChunkDTO chunkDTO = api.getChunkAPI().getRedis().get(new Vector2(chunk.getX(), chunk.getZ()));

                                    if(chunkDTO != null && !chunkDTO.isClaimable()) {
                                        WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(252, 211, 77, 15));
                                        player.sendMessage("This chunk is considered not claimable");
                                        return;
                                    }

                                    if(chunkDTO == null || chunkDTO.getLandId() == null) {
                                        WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(16, 185, 129, 15));
                                        player.sendMessage("This chunk has not been claimed yet.");
                                        return;
                                    }

                                    LandDTO landDTO = api.getLandAPI().getRedis().get(chunkDTO.getLandId());
                                    if(landDTO == null) {
                                        player.sendMessage("Could not find land at current chunk.");
                                        return;
                                    }

                                    MinecraftUserDTO minecraftUserDTO = api.getMinecraftUserAPI().getRedis().get(UUID.fromString(landDTO.getOwnerId()));
                                    if(minecraftUserDTO == null) {
                                        player.sendMessage("Error when trying to find the land owner.");
                                        return;
                                    }

                                    if(!landDTO.getOwnerId().equals(player.getUniqueId().toString())) {
                                        WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(239, 68, 68, 15));
                                    }
                                    else {
                                        WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(59, 130, 246, 15));
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
