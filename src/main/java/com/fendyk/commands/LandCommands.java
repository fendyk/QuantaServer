package com.fendyk.commands;

import com.fendyk.API;
import com.fendyk.DTOs.*;
import com.fendyk.DTOs.updates.UpdateLandDTO;
import com.fendyk.Main;
import com.fendyk.clients.apis.ChunkAPI;
import com.fendyk.configs.MessagesConfig;
import com.fendyk.managers.ConfirmCommandManager;
import com.fendyk.managers.WorldguardSyncManager;
import com.fendyk.utilities.Vector2;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.luckperms.api.model.user.User;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.color.DustData;

import java.util.*;

public class LandCommands {

    Main main = Main.getInstance();

    public LandCommands(Main server) {
        API api = server.getApi();
        new CommandAPICommand("land")
                // ### /land ###
                .executesPlayer((player, args) -> {
                    UUID uuid = player.getUniqueId();

                    LandDTO landDTO = main.getApi().getLandAPI().get(uuid);
                    if (landDTO == null) {
                        player.sendMessage("It looks like that you've not created a land yet. To create one, type: /land create <name>");
                        return;
                    }

                    player.sendMessage("ID: " + landDTO.getId());
                    player.sendMessage("Name: " + landDTO.getName());
                    player.sendMessage("Members: " + landDTO.getMemberIDs().size());
                    player.sendMessage("Homes: " + landDTO.getHomes().size());
                })
                // ### /land help ###
                .withSubcommand(new CommandAPICommand("help")
                        .executesPlayer((player, args) -> {
                            player.sendMessage("/land - To view information about your land");
                            player.sendMessage("/land info - To view information about the land you're standing on");
                            player.sendMessage("/land create <name> - To create your first land");
                            player.sendMessage("/land claim - To claim a chunk you're standing on");
                            player.sendMessage("/land spawn - To visit your land's spawn");
                            player.sendMessage("/land homes - To view all your homes");
                            player.sendMessage("/land home <name> - To visit your home");
                            player.sendMessage("/land home set,remove <name> - To set or remove a home");
                        })
                )
                // ### /land create <name> ###
                .withSubcommand(new CommandAPICommand("create")
                        .withArguments(new StringArgument("name"))
                        .executesPlayer((player, args) -> {
                            String name = (String) args[0];
                            Chunk chunk = player.getChunk();
                            UUID uuid = player.getUniqueId();

                            if (api.getBlacklistedChunkAPI().isBlacklisted(chunk)) {
                                player.sendMessage("The chunk you're currently standing on is considered 'blacklisted' and not claimable.");
                                return;
                            }

                            LandDTO landDTO = main.getApi().getLandAPI().get(uuid);
                            if (landDTO != null) {
                                player.sendMessage("You've already created a land. Lands can only be created once by one player.");
                                return;
                            }

                            try {
                                LandDTO landDTO1 = api.getLandAPI().create(player, name, chunk, player.getLocation());
                                player.sendMessage("Your land has been created");
                                WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(16, 185, 129, 15));
                                ParticleEffect.FIREWORKS_SPARK.display(player.getLocation());
                            } catch (Exception e) {
                                Bukkit.getLogger().info(Arrays.toString(e.getStackTrace()));
                                player.sendMessage(e.getMessage());
                            }
                        })

                )
                // ### /claim ###
                .withSubcommand(new CommandAPICommand("claim")
                        .executesPlayer((player, args) -> {
                            Chunk chunk = player.getChunk();
                            UUID uuid = player.getUniqueId();
                            User user = Main.getInstance().getLuckPermsApi().getUserManager().getUser(uuid);

                            if (user == null) return;

                            // TODO: Validate for worldName

                            if (api.getBlacklistedChunkAPI().getRedis().hGet(new Vector2(chunk.getX(), chunk.getZ()))) {
                                player.sendMessage("The chunk you're currently standing on is considered 'blacklisted' and not claimable.");
                                return;
                            }

                            ChunkDTO chunkDTO = api.getChunkAPI().getRedis().get(new Vector2(chunk.getX(), chunk.getZ()));

                            if (chunkDTO == null) {
                                chunkDTO = api.getChunkAPI().create(chunk, true);

                                if (chunkDTO == null) {
                                    player.sendMessage("Chunk could not be found.");
                                    return;
                                }
                            }

                            String chunkLandId = chunkDTO.getLandId();
                            if (chunkLandId != null) {
                                WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(239, 68, 68, 15));
                                player.sendMessage("This chunk has already been claimed by someone else");
                                return;
                            }

                            MinecraftUserDTO minecraftUserDTO = api.getMinecraftUserAPI().getRedis().get(player.getUniqueId());
                            if (minecraftUserDTO == null) {
                                player.sendMessage("Something went wrong when fetching your data.");
                                return;
                            }

                            LandDTO landDTO = api.getLandAPI().getRedis().get(player.getUniqueId().toString());
                            if (landDTO == null) {
                                player.sendMessage("You currently dont have a land. To create one, type /land create <name>");
                                return;
                            }

                            if (chunkDTO.isClaimable() != null && !chunkDTO.isClaimable()) {
                                WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(252, 211, 77, 15));
                                player.sendMessage("This chunk is considered not claimable");
                                return;
                            }

                            // Find out if there is a neighbour.
                            List<Chunk> neighbours = WorldguardSyncManager.getNeighboringChunks(chunk);
                            long countNeighbours = neighbours.stream().filter(neighbour -> {
                                ChunkDTO neighbourChunkDTO = api.getChunkAPI().getRedis().get(new Vector2(neighbour.getX(), neighbour.getZ()));
                                return neighbourChunkDTO != null && neighbourChunkDTO.getLandId() != null && neighbourChunkDTO.getLandId().equals(landDTO.getId());
                            }).count();

                            if (countNeighbours < 1) {
                                player.sendMessage("You can only claim chunks that are neighbours of you current land.");
                                return;
                            }

                            String primaryGroup = user.getPrimaryGroup();
                            boolean canExpire = primaryGroup.equalsIgnoreCase("default") || primaryGroup.equalsIgnoreCase("barbarian");

                            DateTime expireDate = new DateTime().plusMinutes(2);

                            boolean isClaimed = api.getChunkAPI().claim(chunk, landDTO.getId(), canExpire, canExpire ? expireDate : null);
                            if (!isClaimed) {
                                player.sendMessage("Could not claim chunk.");
                                return;
                            }

                            WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(16, 185, 129, 15));
                            ParticleEffect.FIREWORKS_SPARK.display(player.getLocation());
                            player.sendMessage("Chunk has been claimed.");
                            if (canExpire) {
                                player.sendMessage("This is not permanent and will expire at " + expireDate);
                            }
                        })
                )
                // ### /land spawn ###
                .withSubcommand(new CommandAPICommand("spawn")
                        .executesPlayer((player, args) -> {
                            // Spawn
                        })
                )
                // ### /land info ###
                .withSubcommand(new CommandAPICommand("info")
                        .executesPlayer((player, args) -> {
                            Chunk chunk = player.getChunk();

                            if (api.getBlacklistedChunkAPI().isBlacklisted(chunk)) {
                                player.sendMessage(main.getMessagesConfig().getMessage(MessagesConfig.State.CHUNK_IS_BLACKLISTED));
                                return;
                            }

                            ChunkDTO chunkDTO = api.getChunkAPI().getRedis().get(new Vector2(chunk.getX(), chunk.getZ()));

                            if (chunkDTO == null || chunkDTO.getLandId() == null) {
                                WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(16, 185, 129, 15));
                                player.sendMessage("This chunk has not been claimed yet.");
                                return;
                            }

                            if (chunkDTO.isClaimable() != null && !chunkDTO.isClaimable()) {
                                WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(252, 211, 77, 15));
                                player.sendMessage("This chunk is considered not claimable");
                                return;
                            }

                            LandDTO landDTO = api.getLandAPI().getRedis().get(chunkDTO.getLandId());
                            if (landDTO == null) {
                                player.sendMessage("Could not find land at current chunk.");
                                return;
                            }

                            MinecraftUserDTO minecraftUserDTO = api.getMinecraftUserAPI().getRedis().get(UUID.fromString(landDTO.getOwnerId()));
                            if (minecraftUserDTO == null) {
                                player.sendMessage("Error when trying to find the land owner.");
                                return;
                            }

                            if (!landDTO.getOwnerId().equals(player.getUniqueId().toString())) {
                                WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(239, 68, 68, 15));
                            } else {
                                WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(59, 130, 246, 15));
                            }

                            player.sendMessage("You're currently standing at:");
                            player.sendMessage("Chunk: " + chunkDTO.getX() + "/" + chunkDTO.getZ());
                            player.sendMessage("Land:" + landDTO.getName());
                            player.sendMessage("Owned by player: " + Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(minecraftUserDTO.getId()))).getName());
                            if (chunkDTO.canExpire()) {
                                player.sendMessage("Expiration date:" + chunkDTO.getExpirationDate());
                            }
                        })
                )
                /* HOMES COMMAND */
                .withSubcommand(new CommandAPICommand("homes")
                        .executesPlayer((player, args) -> {
                            UUID uuid = player.getUniqueId();

                            LandDTO landDTO = api.getLandAPI().getRedis().get(uuid.toString());
                            if (landDTO == null) {
                                player.sendMessage("Could not find your land");
                                return;
                            }

                            if (landDTO.getHomes() == null || landDTO.getHomes().isEmpty()) {
                                player.sendMessage("Could not find any homes yet. Maybe it's time to add one? Type /land homes add <name>");
                                return;
                            }

                            for (TaggedLocationDTO home : landDTO.getHomes()) {
                                LocationDTO loc = home.getLocation();
                                player.sendMessage(home.getName() + ": (" + loc.getX() + "," + loc.getY() + "," + loc.getZ() + ")");
                            }
                        })
                )
                // ### /land home <name> ###
                .withSubcommand(new CommandAPICommand("home")
                        .withArguments(new StringArgument("name"))
                        .executesPlayer((player, args) -> {
                            String name = (String) args[0];
                            UUID uuid = player.getUniqueId();
                            World world = Bukkit.getWorld(server.getServerConfig().getWorldName());

                            LandDTO landDTO = api.getLandAPI().getRedis().get(uuid.toString());
                            if (landDTO == null) {
                                player.sendMessage("Could not find your land");
                                return;
                            }

                            Optional<TaggedLocationDTO> taggedLocationDTO = landDTO.getHomes().stream().filter(t -> t.getName().equals(name)).findFirst();

                            if (taggedLocationDTO.isEmpty()) {
                                player.sendMessage("Could not find any home matching the name: " + name);
                                return;
                            }

                            LocationDTO loc = taggedLocationDTO.get().getLocation();

                            player.teleport(new Location(world, loc.getX(), loc.getY(), loc.getZ(), (float) loc.getYaw(), (float) loc.getPitch()));
                            player.sendMessage(player.getName() + " You have been teleported to " + name);
                        })
                        // ### /land home set <name> ###
                        .withSubcommand(new CommandAPICommand("set")
                                .withArguments(new StringArgument("name"))
                                .executesPlayer((player, args) -> {
                                    String name = (String) args[0];
                                    UUID uuid = player.getUniqueId();

                                    UpdateLandDTO updateLandDTO = new UpdateLandDTO();
                                    LandDTO landDTO = api.getLandAPI().getRedis().get(uuid.toString());
                                    if (landDTO == null) {
                                        player.sendMessage("Could not find your land");
                                        return;
                                    } else if (landDTO.getHomes() != null && landDTO.getHomes().stream().filter(taggedLocationDTO -> taggedLocationDTO.getName().equals(name)).count() > 1) {
                                        player.sendMessage("You've already set a home with the name " + name);
                                        return;
                                    }

                                    LocationDTO locationDTO = new LocationDTO(player.getLocation());
                                    TaggedLocationDTO taggedLocationDTO = new TaggedLocationDTO();
                                    taggedLocationDTO.setName(name);
                                    taggedLocationDTO.setLocation(locationDTO);

                                    updateLandDTO.getPushHomes().add(taggedLocationDTO);
                                    LandDTO result = api.getLandAPI().getFetch().update(landDTO.getId(), updateLandDTO);

                                    if (result == null) {
                                        player.sendMessage("Could not update your land's home.");
                                        return;
                                    }

                                    player.sendMessage(name + " has been added to your land's homes");
                                })
                        )
                        // ### /land home remove <name> ###
                        .withSubcommand(new CommandAPICommand("remove")
                                .withArguments(new StringArgument("name"))
                                .executesPlayer((player, args) -> {
                                    String name = (String) args[0];
                                    UUID uuid = player.getUniqueId();

                                    UpdateLandDTO updateLandDTO = new UpdateLandDTO();
                                    LandDTO landDTO = api.getLandAPI().getRedis().get(uuid.toString());
                                    if (landDTO == null) {
                                        player.sendMessage("Could not find your land");
                                        return;
                                    } else if (landDTO.getHomes() != null && landDTO.getHomes().stream().noneMatch(taggedLocationDTO -> taggedLocationDTO.getName().equals(name))) {
                                        player.sendMessage("Could not find a home with the name: " + name);
                                        return;
                                    }

                                    updateLandDTO.getSpliceHomes().add(name);
                                    LandDTO result = api.getLandAPI().getFetch().update(landDTO.getId(), updateLandDTO);

                                    if (result == null) {
                                        player.sendMessage("Could not update your land's home.");
                                        return;
                                    }

                                    player.sendMessage(name + " has been removed from your land's homes");
                                })
                        )
                )
                // ### /land members ###
                .withSubcommand(new CommandAPICommand("members")
                        .executesPlayer((player, args) -> {
                            UUID uuid = player.getUniqueId();

                            LandDTO landDTO = api.getLandAPI().getRedis().get(uuid.toString());
                            if (landDTO == null) {
                                player.sendMessage("Could not find your land");
                                return;
                            }

                            if (landDTO.getMemberIDs() == null || landDTO.getMemberIDs().isEmpty()) {
                                player.sendMessage("Could not find any homes yet. Maybe it's time to add one? Type /land homes set");
                                return;
                            }

                            for (String member : landDTO.getMemberIDs()) {
                                player.sendMessage(Bukkit.getOfflinePlayer(UUID.fromString(member)).getName() + " is a member of your land");
                            }
                        })
                )
                // ### /land member ###
                .withSubcommand(new CommandAPICommand("member")
                        // ### /land member add <name> ###
                        .withSubcommand(new CommandAPICommand("add")
                                .withArguments(new StringArgument("name"))
                                .executesPlayer((player, args) -> {
                                    OfflinePlayer newMember = (OfflinePlayer) args[0];
                                    UUID uuid = player.getUniqueId();
                                    UUID memberUuid = newMember.getUniqueId();

                                    UpdateLandDTO updateLandDTO = new UpdateLandDTO();
                                    LandDTO landDTO = api.getLandAPI().getRedis().get(uuid.toString());
                                    if (landDTO == null) {
                                        player.sendMessage("Could not find your land");
                                        return;
                                    } else if (landDTO.getMemberIDs() != null && landDTO.getMemberIDs().contains(memberUuid.toString())) {
                                        player.sendMessage("Player is already a member");
                                        return;
                                    }

                                    updateLandDTO.getConnectMembers().add(new UpdateLandDTO.MemberDTO(memberUuid.toString()));
                                    LandDTO result = api.getLandAPI().getFetch().update(landDTO.getId(), updateLandDTO);

                                    if (result == null) {
                                        player.sendMessage("Could not update your land member.");
                                        return;
                                    }

                                    player.sendMessage(newMember.getName() + " has been added as a member to your land");
                                })
                        )
                        // ### /land member remove <name> ###
                        .withSubcommand(new CommandAPICommand("remove")
                                .withArguments(new StringArgument("name"))
                                .executesPlayer((player, args) -> {
                                    OfflinePlayer oldMember = (OfflinePlayer) args[0];
                                    UUID uuid = player.getUniqueId();
                                    UUID memberUuid = oldMember.getUniqueId();

                                    UpdateLandDTO updateLandDTO = new UpdateLandDTO();
                                    LandDTO landDTO = api.getLandAPI().getRedis().get(uuid.toString());
                                    if (landDTO == null) {
                                        player.sendMessage("Could not find your land");
                                        return;
                                    } else if (landDTO.getMemberIDs() != null && !landDTO.getMemberIDs().contains(memberUuid.toString())) {
                                        player.sendMessage("Player is not a member");
                                        return;
                                    }

                                    updateLandDTO.getDisconnectMembers().add(new UpdateLandDTO.MemberDTO(memberUuid.toString()));
                                    LandDTO result = api.getLandAPI().getFetch().update(landDTO.getId(), updateLandDTO);

                                    if (result == null) {
                                        player.sendMessage("Could not update your land member.");
                                        return;
                                    }

                                    player.sendMessage(oldMember.getName() + " has been removed from your land");
                                })
                        )
                )
                // ### /land generate ###
                .withSubcommand(new CommandAPICommand("generate")
                        .withPermission(CommandPermission.OP)
                        .withArguments(new BooleanArgument("isClaimable"))
                        .executesPlayer((player, args) -> {
                            boolean isClaimable = (boolean) args[0];
                            Chunk chunk = player.getChunk();

                            ChunkDTO chunkDTO = api.getChunkAPI().create(chunk, isClaimable);
                            if (chunkDTO == null) {
                                player.sendMessage("Error when trying to create a chunk");
                                return;
                            }

                            player.sendMessage("Chunk has been generated");
                        })
                )
                // ### /land extend ###
                .withSubcommand(new CommandAPICommand("extend")
                        .executesPlayer((player, args) -> {
                            Chunk chunk = player.getChunk();

                            if (api.getBlacklistedChunkAPI().isBlacklisted(chunk)) {
                                player.sendMessage(main.getMessagesConfig().getMessage(MessagesConfig.State.CHUNK_IS_BLACKLISTED));
                                return;
                            }

                            ChunkDTO chunkDTO = main.getApi().getChunkAPI().get(chunk);

                            if (chunkDTO == null || ChunkAPI.isClaimable(chunkDTO)) {
                                player.sendMessage(main.getMessagesConfig().getMessage(MessagesConfig.State.CHUNK_IS_NOT_CLAIMABLE));
                                return;
                            }

                        })
                )
                .register();
    }

}
