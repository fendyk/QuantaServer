package com.fendyk.commands;

import com.fendyk.API;
import com.fendyk.DTOs.*;
import com.fendyk.DTOs.updates.UpdateChunkDTO;
import com.fendyk.DTOs.updates.UpdateLandDTO;
import com.fendyk.Main;
import com.fendyk.clients.apis.ChunkAPI;
import com.fendyk.clients.apis.LandAPI;
import com.fendyk.clients.apis.MinecraftUserAPI;
import com.fendyk.configs.MessagesConfig;
import com.fendyk.configs.ServerConfig;
import com.fendyk.managers.ConfirmCommandManager;
import com.fendyk.managers.WorldguardSyncManager;
import com.fendyk.utilities.*;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.model.user.User;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.joda.time.DateTime;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.color.DustData;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LandCommands {

    Main main = Main.instance;

    public LandCommands() {
        final API api = main.api;
        final ServerConfig serverConfig = main.serverConfig;
        final LandAPI landAPI = api.landAPI;
        final ChunkAPI chunkAPI = api.chunkAPI;
        final MinecraftUserAPI minecraftUserAPI = api.minecraftUserAPI;
        final World overworld = serverConfig.getOverworld();


        new CommandAPICommand("land")
                // ### /land ###
                .executesPlayer((player, args) -> {
                    UUID uuid = player.getUniqueId();

                    LandDTO landDTO = landAPI.get(uuid);
                    if (landDTO == null) {
                        player.sendMessage(PluginTextComponent.warning(
                                "You have not created a land yet. To create one," +
                                        " type /land create <name>"
                        ));
                        return;
                    }

                    player.sendMessage(PluginTextComponent.statistic("ID:", landDTO.id));
                    player.sendMessage(PluginTextComponent.statistic("Name:", landDTO.name));
                    player.sendMessage(PluginTextComponent.statistic("Member count:", String.valueOf(landDTO.memberIDs.size())));
                    player.sendMessage(PluginTextComponent.statistic("Home count:", String.valueOf(landDTO.homes.size())));
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
                            player.sendMessage("/land home set,remove,tp <name> - To set, remove or teleport to a home");
                        })
                )
                // ### /land create <name> ###
                .withSubcommand(new CommandAPICommand("create")
                        .withArguments(new StringArgument("name"))
                        .executesPlayer((player, args) -> {
                            String name = (String) args[0];
                            Chunk chunk = player.getChunk();
                            UUID uuid = player.getUniqueId();

                            // Only continue if we're in the OverWorld
                            if(!chunk.getWorld().equals(overworld)) {
                                player.sendMessage(PluginTextComponent.error(
                                        "You can only create land in the overworld")
                                );
                                return;
                            }

                            // Check if the player is within the blacklisted chunk radius
                            if(serverConfig.isWithinBlacklistedChunkRadius(player.getLocation())) {
                                player.sendMessage(PluginTextComponent.warning(
                                        "The chunk you're currently standing on is considered " +
                                                "'blacklisted' and not claimable."
                                ));
                                return;
                            }

                            LandDTO landDTO = landAPI.get(uuid);
                            if (landDTO != null) {
                                player.sendMessage(PluginTextComponent.warning(
                                        "You've already created a land. Lands can only be created once by one player."
                                ));
                                return;
                            }

                            ValidateCommand validateCommand = new ValidateCommand.Builder(player)
                                    .checkPrimaryGroup()
                                    .checkRankConfiguration()
                                    .build();

                            if(!validateCommand.passed()) {
                                player.sendMessage(PluginTextComponent.error(
                                        "We could not pass the validation of the command."
                                ));
                                return;
                            }

                            ValidateCommand.Builder builder = validateCommand.getBuilder();
                            RankConfiguration rankConfiguration = builder.getRankConfiguration();

                            if (!ConfirmCommandManager.isConfirmed(player)) {
                                ConfirmCommandManager.requestCommandConfirmation(player,
                                        new PayableCommand(
                                                "/land create " + name,
                                                new ArrayList<>(),
                                                main.pricesConfig.getLandCreatePrice(),
                                                60L,
                                                rankConfiguration.getDiscountPercentage()
                                        )
                                );
                                return;
                            }

                            try {
                                LandDTO landDTO1 = landAPI.create(player, name, chunk, player.getLocation());
                                WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(16, 185, 129, 15));
                                ParticleEffect.FIREWORKS_SPARK.display(player.getLocation());
                                player.sendMessage("Your land has been created");
                                Bukkit.broadcast( Component.text(player.getName() + " has created a new land called '" + landDTO1.name + "' somewhere in the universe!", NamedTextColor.GREEN));
                            } catch (Exception e) {
                                Bukkit.getLogger().info(Arrays.toString(e.getStackTrace()));
                                player.sendMessage(e.getMessage());
                            }
                        })

                )
                // ### /claim ###
                .withSubcommand(new CommandAPICommand("claim")
                        .withSubcommand(new CommandAPICommand("expirable")
                                .executesPlayer((player, args) -> {
                                    Chunk chunk = player.getChunk();
                                    UUID uuid = player.getUniqueId();

                                    // Only continue if we're in the OverWorld
                                    if(!chunk.getWorld().equals(overworld)) {
                                        player.sendMessage(PluginTextComponent.error(
                                                "You can only create land in the overworld")
                                        );
                                        return;
                                    }

                                    // Check if the player is within the blacklisted chunk radius
                                    if(serverConfig.isWithinBlacklistedChunkRadius(player.getLocation())) {
                                        player.sendMessage(PluginTextComponent.warning(
                                                "The chunk you're currently standing on is considered " +
                                                        "'blacklisted' and not claimable."
                                        ));
                                        return;
                                    }

                                    LandDTO landDTO = api.landAPI.redis.get(player.getUniqueId().toString());
                                    if (landDTO == null) {
                                        player.sendMessage(PluginTextComponent.warning(
                                                "You currently dont have a land. To create one," +
                                                        " type /land create <name>"
                                        ));
                                        return;
                                    }

                                    ValidateCommand validateCommand = new ValidateCommand.Builder(player)
                                            .checkPrimaryGroup()
                                            .checkRankConfiguration()
                                            .checkMinecraftUserDTO()
                                            .build();

                                    if(!validateCommand.passed()) {
                                        player.sendMessage("We could not pass the validation of the command.");
                                        return;
                                    }

                                    ValidateCommand.Builder builder = validateCommand.getBuilder();
                                    RankConfiguration rankConfiguration = builder.getRankConfiguration();
                                    MinecraftUserDTO minecraftUserDTO = builder.getMinecraftUserDTO();
                                    ChunkDTO chunkDTO = api.chunkAPI.redis.get(new Vector2(chunk.getX(), chunk.getZ()));

                                    if (chunkDTO == null) {
                                        chunkDTO = api.chunkAPI.create(chunk, true);

                                        if (chunkDTO == null) {
                                            player.sendMessage("Chunk could not be found.");
                                            return;
                                        }
                                    }

                                    String chunkLandId = chunkDTO.landId;
                                    if (chunkLandId != null) {
                                        WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(239, 68, 68, 15));
                                        player.sendMessage("This chunk has already been claimed by someone else");
                                        return;
                                    }

                                    if (chunkDTO.isClaimable != null && !chunkDTO.isClaimable) {
                                        WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(252, 211, 77, 15));
                                        player.sendMessage("This chunk is considered not claimable");
                                        return;
                                    }

                                    // Find out if there is a neighbour.
                                    List<Chunk> neighbours = ChunkUtils.getNeighboringChunks(chunk);
                                    long countNeighbours = neighbours.stream().filter(neighbour -> {
                                        ChunkDTO neighbourChunkDTO = api.chunkAPI.redis.get(new Vector2(neighbour.getX(), neighbour.getZ()));
                                        return neighbourChunkDTO != null && neighbourChunkDTO.landId != null && neighbourChunkDTO.landId.equals(landDTO.id);
                                    }).count();

                                    if (countNeighbours < 1) {
                                        player.sendMessage("You can only claim chunks that are neighbours of you current land.");
                                        return;
                                    }

                                    ArrayList<ChunkDTO> chunkDTOS = landDTO.chunks;

                                    if(chunkDTOS != null) {
                                        ArrayList<ChunkDTO> expirableChunkDTOS = chunkDTOS.stream()
                                                .filter(ChunkDTO::canExpire)
                                                .collect(Collectors.toCollection(ArrayList::new));

                                        if(expirableChunkDTOS.size() >= rankConfiguration.getRenewableChunkSlots()) {
                                            player.sendMessage("You've reached your limit for claiming new renewable chunks.");
                                            return;
                                        }
                                    }

                                    if (!ConfirmCommandManager.isConfirmed(player)) {
                                        ConfirmCommandManager.requestCommandConfirmation(player,
                                                new PayableCommand(
                                                        "/land claim expirable",
                                                        new ArrayList<>(),
                                                        main.pricesConfig.getLandClaimExpirablePrice(),
                                                        30L,
                                                        rankConfiguration.getDiscountPercentage()
                                                )
                                        );
                                        return;
                                    }

                                    DateTime expireDate = new DateTime().plusDays(7);
                                    boolean isClaimed = api.chunkAPI.claim(chunk, landDTO.id, true, expireDate);
                                    if (!isClaimed) {
                                        player.sendMessage("Could not claim chunk.");
                                        return;
                                    }

                                    WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(16, 185, 129, 15));
                                    ParticleEffect.FIREWORKS_SPARK.display(player.getLocation());
                                    player.sendMessage("Chunk has been claimed.");
                                    player.sendMessage("This is not permanent and will expire at " + expireDate);
                                    Bukkit.broadcast( Component.text(player.getName() + " has claimed a renewable chunk somewhere in the universe!", NamedTextColor.GREEN));
                                })
                        )
                        .withSubcommand(new CommandAPICommand("permanent")
                                .executesPlayer((player, args) -> {
                                    Chunk chunk = player.getChunk();
                                    UUID uuid = player.getUniqueId();

                                    // Only continue if we're in the overworld
                                    if(!chunk.getWorld().equals(main.serverConfig.getOverworld())) {
                                        player.sendMessage("You can only claim chunks in the overworld.");
                                        return;
                                    }

                                    // Check if the player is within the blacklisted chunk radius
                                    if(main.serverConfig.isWithinBlacklistedChunkRadius(player.getLocation())) {
                                        player.sendMessage("The chunk you're currently standing on is considered 'blacklisted' and not claimable.");
                                        return;
                                    }

                                    LandDTO landDTO = api.landAPI.redis.get(player.getUniqueId().toString());
                                    if (landDTO == null) {
                                        player.sendMessage("You currently dont have a land. To create one, type /land create <name>");
                                        return;
                                    }

                                    ValidateCommand validateCommand = new ValidateCommand.Builder(player)
                                            .checkPrimaryGroup()
                                            .checkRankConfiguration()
                                            .checkMinecraftUserDTO()
                                            .build();

                                    if(!validateCommand.passed()) {
                                        player.sendMessage("We could not pass the validation of the command.");
                                        return;
                                    }

                                    ValidateCommand.Builder builder = validateCommand.getBuilder();
                                    RankConfiguration rankConfiguration = builder.getRankConfiguration();
                                    MinecraftUserDTO minecraftUserDTO = builder.getMinecraftUserDTO();
                                    ChunkDTO chunkDTO = api.chunkAPI.redis.get(new Vector2(chunk.getX(), chunk.getZ()));

                                    if (chunkDTO == null) {
                                        chunkDTO = api.chunkAPI.create(chunk, true);

                                        if (chunkDTO == null) {
                                            player.sendMessage("Chunk could not be found.");
                                            return;
                                        }
                                    }

                                    String chunkLandId = chunkDTO.landId;
                                    if (chunkLandId != null) {
                                        WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(239, 68, 68, 15));
                                        player.sendMessage("This chunk has already been claimed by someone else");
                                        return;
                                    }

                                    if (chunkDTO.isClaimable != null && !chunkDTO.isClaimable) {
                                        WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(252, 211, 77, 15));
                                        player.sendMessage("This chunk is considered not claimable");
                                        return;
                                    }

                                    // Find out if there is a neighbour.
                                    List<Chunk> neighbours = ChunkUtils.getNeighboringChunks(chunk);
                                    long countNeighbours = neighbours.stream().filter(neighbour -> {
                                        ChunkDTO neighbourChunkDTO = api.chunkAPI.redis.get(new Vector2(neighbour.getX(), neighbour.getZ()));
                                        return neighbourChunkDTO != null && neighbourChunkDTO.landId != null && neighbourChunkDTO.landId.equals(landDTO.id);
                                    }).count();

                                    if (countNeighbours < 1) {
                                        player.sendMessage("You can only claim chunks that are neighbours of you current land.");
                                        return;
                                    }

                                    ArrayList<ChunkDTO> chunkDTOS = landDTO.chunks;

                                    if(chunkDTOS != null && chunkDTOS.size() >= rankConfiguration.getChunkSlots()) {
                                        player.sendMessage("You've reached your limit for claiming new permanent chunks.");
                                        return;
                                    }

                                    if (!ConfirmCommandManager.isConfirmed(player)) {
                                        ConfirmCommandManager.requestCommandConfirmation(player,
                                                new PayableCommand(
                                                        "/land claim permanent",
                                                        new ArrayList<>(),
                                                        main.pricesConfig.getLandClaimPermanentPrice(),
                                                        30L,
                                                        rankConfiguration.getDiscountPercentage()
                                                )
                                        );
                                        return;
                                    }

                                    boolean isClaimed = api.chunkAPI.claim(chunk, landDTO.id, false, null);
                                    if (!isClaimed) {
                                        player.sendMessage("Could not claim chunk.");
                                        return;
                                    }

                                    WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(16, 185, 129, 15));
                                    ParticleEffect.FIREWORKS_SPARK.display(player.getLocation());
                                    player.sendMessage("Chunk has been claimed.");
                                    Bukkit.broadcast( Component.text(player.getName() + " has claimed a permanent chunk somewhere in the universe!", NamedTextColor.GREEN));
                                })
                        )
                )
                // ### /land spawn ###
                .withSubcommand(new CommandAPICommand("spawn")
                        .executesPlayer((player, args) -> {
                            UUID uuid = player.getUniqueId();
                            World world = main.serverConfig.getOverworld();

                            LandDTO landDTO = api.landAPI.redis.get(uuid.toString());
                            if (landDTO == null) {
                                player.sendMessage("Could not find your land");
                                return;
                            }

                            Optional<TaggedLocationDTO> taggedLocationDTO = Optional.ofNullable(landDTO.homes.stream()
                                    .filter(t -> t.name.equals("spawn"))
                                    .findAny()
                                    .orElseGet(() -> landDTO.homes.stream().findFirst().orElse(null)));

                            if (taggedLocationDTO.isEmpty()) {
                                player.sendMessage("Could not locate your spawn location. Try adding a home naming 'spawn'");
                                return;
                            }

                            ValidateCommand validateCommand = new ValidateCommand.Builder(player)
                                    .checkPrimaryGroup()
                                    .checkRankConfiguration()
                                    .build();

                            if(!validateCommand.passed()) {
                                player.sendMessage("We could not pass the validation of the command.");
                                return;
                            }
                            ValidateCommand.Builder builder = validateCommand.getBuilder();
                            RankConfiguration rankConfiguration = builder.getRankConfiguration();

                            if (!ConfirmCommandManager.isConfirmed(player)) {
                                ConfirmCommandManager.requestCommandConfirmation(player,
                                        new PayableCommand(
                                                "/land spawn",
                                                new ArrayList<>(),
                                                main.pricesConfig.getTeleportCommandPrice(),
                                                30L,
                                                rankConfiguration.getDiscountPercentage()
                                        )
                                );
                                return;
                            }

                            LocationDTO loc = taggedLocationDTO.get().location;

                            player.teleport(new Location(world, loc.x, loc.y, loc.z, (float) loc.yaw, (float) loc.pitch));
                            player.sendMessage(player.getName() + " You have been teleported to your spawn");
                        })
                )
                // ### /land info ###
                .withSubcommand(new CommandAPICommand("info")
                        .executesPlayer((player, args) -> {
                            Chunk chunk = player.getChunk();

                            // Only continue if we're in the overworld
                            if(!chunk.getWorld().equals(main.serverConfig.getOverworld())) {
                                player.sendMessage("You can only see information about a chunk in the overworld");
                                return;
                            }

                            // Check if the player is within the blacklisted chunk radius
                            if(main.serverConfig.isWithinBlacklistedChunkRadius(player.getLocation())) {
                                player.sendMessage("The chunk you're currently standing on is considered 'blacklisted' and not claimable.");
                                return;
                            }

                            ChunkDTO chunkDTO = api.chunkAPI.redis.get(new Vector2(chunk.getX(), chunk.getZ()));

                            if (chunkDTO == null || chunkDTO.landId == null) {
                                WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(16, 185, 129, 15));
                                player.sendMessage("This chunk has not been claimed yet.");
                                return;
                            }

                            if (chunkDTO.isClaimable != null && !chunkDTO.isClaimable) {
                                WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(252, 211, 77, 15));
                                player.sendMessage("This chunk is considered not claimable");
                                return;
                            }

                            LandDTO landDTO = api.landAPI.redis.get(chunkDTO.landId);
                            if (landDTO == null) {
                                player.sendMessage("Could not find land at current chunk.");
                                return;
                            }

                            CompletableFuture<MinecraftUserDTO> awaitMinecraftUser = minecraftUserAPI.get(UUID.fromString(landDTO.ownerId));
                            // Add more here

                            // Perform actions
                            CompletableFuture.allOf(awaitMinecraftUser).handleAsync((unused, ex) -> {
                                if(ex != null) {
                                    player.sendMessage(ex.getMessage());
                                    return null;
                                }
                                MinecraftUserDTO minecraftUserDTO = awaitMinecraftUser.join();
                                // More to be joined here

                                // Perform additional tasks with user1 and user2


                                return null;
                            });

                            if (!landDTO.ownerId.equals(player.getUniqueId().toString())) {
                                WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(239, 68, 68, 15));
                            } else {
                                WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(59, 130, 246, 15));
                            }

                            player.sendMessage("You're currently standing at:");
                            player.sendMessage("Chunk: " + chunkDTO.x + "/" + chunkDTO.z);
                            player.sendMessage("Land:" + landDTO.name);
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

                            LandDTO landDTO = api.landAPI.redis.get(uuid.toString());
                            if (landDTO == null) {
                                player.sendMessage("Could not find your land");
                                return;
                            }

                            if (landDTO.homes == null || landDTO.homes.isEmpty()) {
                                player.sendMessage("Could not find any homes yet. Maybe it's time to add one? Type /land homes add <name>");
                                return;
                            }

                            for (TaggedLocationDTO home : landDTO.homes) {
                                LocationDTO loc = home.location;
                                player.sendMessage(home.name + ": (" + loc.x + "," + loc.y + "," + loc.z + ")");
                            }
                        })
                )
                // ### /land home <name> ###
                .withSubcommand(new CommandAPICommand("home")
                        .withSubcommand(new CommandAPICommand("tp")
                                .withArguments(new StringArgument("name"))
                                .executesPlayer((player, args) -> {
                                    String name = (String) args[0];
                                    UUID uuid = player.getUniqueId();
                                    World world = main.serverConfig.getOverworld();

                                    LandDTO landDTO = api.landAPI.redis.get(uuid.toString());
                                    if (landDTO == null) {
                                        player.sendMessage("Could not find your land");
                                        return;
                                    }

                                    Optional<TaggedLocationDTO> taggedLocationDTO = landDTO.homes.stream().filter(t -> t.name.equals(name)).findFirst();

                                    if (taggedLocationDTO.isEmpty()) {
                                        player.sendMessage("Could not find any home matching the name: " + name);
                                        return;
                                    }

                                    ValidateCommand validateCommand = new ValidateCommand.Builder(player)
                                            .checkPrimaryGroup()
                                            .checkRankConfiguration()
                                            .build();

                                    if(!validateCommand.passed()) {
                                        player.sendMessage("We could not pass the validation of the command.");
                                        return;
                                    }
                                    ValidateCommand.Builder builder = validateCommand.getBuilder();
                                    RankConfiguration rankConfiguration = builder.getRankConfiguration();

                                    if (!ConfirmCommandManager.isConfirmed(player)) {
                                        ConfirmCommandManager.requestCommandConfirmation(player,
                                                new PayableCommand(
                                                        "/land home tp " + name,
                                                        new ArrayList<>(),
                                                        main.pricesConfig.getTeleportCommandPrice(),
                                                        30L,
                                                        rankConfiguration.getDiscountPercentage()
                                                )
                                        );
                                        return;
                                    }

                                    LocationDTO loc = taggedLocationDTO.get().location;

                                    player.teleport(new Location(world, loc.x, loc.y, loc.z, (float) loc.yaw, (float) loc.pitch));
                                    player.sendMessage(player.getName() + " You have been teleported to " + name);
                                })
                        )
                        // ### /land home set <name> ###
                        .withSubcommand(new CommandAPICommand("set")
                                .withArguments(new StringArgument("name"))
                                .executesPlayer((player, args) -> {
                                    String name = (String) args[0];
                                    UUID uuid = player.getUniqueId();
                                    Chunk chunk = player.getChunk();

                                    // Only continue if we're in the overworld
                                    if(!chunk.getWorld().equals(main.serverConfig.getOverworld())) {
                                        player.sendMessage("You can only claim chunks in the overworld.");
                                        return;
                                    }

                                    UpdateLandDTO updateLandDTO = new UpdateLandDTO();
                                    LandDTO landDTO = api.landAPI.redis.get(uuid.toString());
                                    if (landDTO == null) {
                                        player.sendMessage("Could not find your land");
                                        return;
                                    } else if (landDTO.homes != null && landDTO.homes.stream().filter(taggedLocationDTO -> taggedLocationDTO.name.equals(name)).count() > 1) {
                                        player.sendMessage("You've already set a home with the name " + name);
                                        return;
                                    }

                                    ValidateCommand validateCommand = new ValidateCommand.Builder(player)
                                            .checkPrimaryGroup()
                                            .checkRankConfiguration()
                                            .build();

                                    if(!validateCommand.passed()) {
                                        player.sendMessage("We could not pass the validation of the command.");
                                        return;
                                    }

                                    ValidateCommand.Builder builder = validateCommand.getBuilder();
                                    RankConfiguration rankConfiguration = builder.getRankConfiguration();

                                    ArrayList<TaggedLocationDTO> taggedLocationDTOS = landDTO.homes;
                                    if(taggedLocationDTOS != null && taggedLocationDTOS.size() >= rankConfiguration.getHomeSlots()) {
                                        player.sendMessage("You've reached your limit for setting new land homes.");
                                        return;
                                    }

                                    if (!ConfirmCommandManager.isConfirmed(player)) {
                                        ConfirmCommandManager.requestCommandConfirmation(player,
                                                new PayableCommand(
                                                        "/land home set " + name,
                                                        new ArrayList<>(),
                                                        main.pricesConfig.getLandHomeSetPrice(),
                                                        30L,
                                                        rankConfiguration.getDiscountPercentage()
                                                )
                                        );
                                        return;
                                    }

                                    LocationDTO locationDTO = new LocationDTO(player.getLocation());
                                    TaggedLocationDTO taggedLocationDTO = new TaggedLocationDTO();
                                    taggedLocationDTO.name = name;
                                    taggedLocationDTO.location = locationDTO;

                                    updateLandDTO.pushHomes.add(taggedLocationDTO);
                                    LandDTO result = api.landAPI.fetch.update(landDTO.id, updateLandDTO);

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
                                    LandDTO landDTO = api.landAPI.redis.get(uuid.toString());
                                    if (landDTO == null) {
                                        player.sendMessage("Could not find your land");
                                        return;
                                    } else if (landDTO.homes != null && landDTO.homes.stream().noneMatch(taggedLocationDTO -> taggedLocationDTO.name.equals(name))) {
                                        player.sendMessage("Could not find a home with the name: " + name);
                                        return;
                                    }

                                    updateLandDTO.spliceHomes.add(name);
                                    LandDTO result = api.landAPI.fetch.update(landDTO.id, updateLandDTO);

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

                            LandDTO landDTO = api.landAPI.redis.get(uuid.toString());
                            if (landDTO == null) {
                                player.sendMessage("Could not find your land");
                                return;
                            }

                            if (landDTO.memberIDs == null || landDTO.memberIDs.isEmpty()) {
                                player.sendMessage("Could not find any homes yet. Maybe it's time to add one? Type /land homes set");
                                return;
                            }

                            for (String member : landDTO.memberIDs) {
                                player.sendMessage(Bukkit.getOfflinePlayer(UUID.fromString(member)).getName() + " is a member of your land");
                            }
                        })
                )
                // ### /land member ###
                .withSubcommand(new CommandAPICommand("member")
                        // ### /land member add <name> ###
                        .withSubcommand(new CommandAPICommand("add")
                                .withArguments(new OfflinePlayerArgument("name"))
                                .executesPlayer((player, args) -> {
                                    OfflinePlayer newMember = (OfflinePlayer) args[0];
                                    UUID uuid = player.getUniqueId();
                                    UUID memberUuid = newMember.getUniqueId();

                                    UpdateLandDTO updateLandDTO = new UpdateLandDTO();
                                    LandDTO landDTO = api.landAPI.redis.get(uuid.toString());
                                    if (landDTO == null) {
                                        player.sendMessage("Could not find your land");
                                        return;
                                    } else if (landDTO.memberIDs != null && landDTO.memberIDs.contains(memberUuid.toString())) {
                                        player.sendMessage("Player is already a member");
                                        return;
                                    }

                                    ValidateCommand validateCommand = new ValidateCommand.Builder(player)
                                            .checkPrimaryGroup()
                                            .checkRankConfiguration()
                                            .build();

                                    if(!validateCommand.passed()) {
                                        player.sendMessage("We could not pass the validation of the command.");
                                        return;
                                    }

                                    ValidateCommand.Builder builder = validateCommand.getBuilder();
                                    RankConfiguration rankConfiguration = builder.getRankConfiguration();

                                    ArrayList<String> memberIDs = landDTO.memberIDs;
                                    if(memberIDs != null && memberIDs.size() >= rankConfiguration.getMemberSlots()) {
                                        player.sendMessage("You've reached your limit for setting new land homes.");
                                        return;
                                    }

                                    if (!ConfirmCommandManager.isConfirmed(player)) {
                                        ConfirmCommandManager.requestCommandConfirmation(player,
                                                new PayableCommand(
                                                        "/land member add " + newMember.getName(),
                                                        new ArrayList<>(),
                                                        main.pricesConfig.getLandMemberAddPrice(),
                                                        30L,
                                                        rankConfiguration.getDiscountPercentage()
                                                )
                                        );
                                        return;
                                    }

                                    updateLandDTO.connectMembers.add(new UpdateLandDTO.MemberDTO(memberUuid.toString()));
                                    LandDTO result = api.landAPI.fetch.update(landDTO.id, updateLandDTO);

                                    if (result == null) {
                                        player.sendMessage("Could not update your land member.");
                                        return;
                                    }

                                    player.sendMessage(newMember.getName() + " has been added as a member to your land");
                                })
                        )
                        // ### /land member remove <name> ###
                        .withSubcommand(new CommandAPICommand("remove")
                                .withArguments(new PlayerArgument("name"))
                                .executesPlayer((player, args) -> {
                                    OfflinePlayer oldMember = (OfflinePlayer) args[0];
                                    UUID uuid = player.getUniqueId();
                                    UUID memberUuid = oldMember.getUniqueId();

                                    UpdateLandDTO updateLandDTO = new UpdateLandDTO();
                                    LandDTO landDTO = api.landAPI.redis.get(uuid.toString());
                                    if (landDTO == null) {
                                        player.sendMessage("Could not find your land");
                                        return;
                                    } else if (landDTO.memberIDs != null && !landDTO.memberIDs.contains(memberUuid.toString())) {
                                        player.sendMessage("Player is not a member");
                                        return;
                                    }

                                    updateLandDTO.disconnectMembers.add(new UpdateLandDTO.MemberDTO(memberUuid.toString()));
                                    LandDTO result = api.landAPI.fetch.update(landDTO.id, updateLandDTO);

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
                        .withRequirement(ServerOperator::isOp)
                        .withPermission(CommandPermission.OP)
                        .withArguments(new BooleanArgument("isClaimable"))
                        .executesPlayer((player, args) -> {
                            boolean isClaimable = (boolean) args[0];
                            Chunk chunk = player.getChunk();

                            // Only continue if we're in the overworld
                            if(!chunk.getWorld().equals(main.serverConfig.getOverworld())) {
                                player.sendMessage("You can only generate chunks in the overworld.");
                                return;
                            }


                            ChunkDTO chunkDTO = api.chunkAPI.create(chunk, isClaimable);
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
                            final Chunk chunk = player.getChunk();
                            final UUID playerUuid = player.getUniqueId();
                            final World currentWorld = chunk.getWorld();

                            // Only continue if we're in the OverWorld
                            if(!currentWorld.equals(overworld)) {
                                player.sendMessage("You can only extend chunks in the overworld.");
                                return;
                            }

                            // Check if the player is within the blacklisted chunk radius
                            if(serverConfig.isWithinBlacklistedChunkRadius(player.getLocation())) {
                                player.sendMessage("The chunk you're currently standing on is considered 'blacklisted' and not claimable.");
                                return;
                            }

                            ChunkDTO chunkDTO = main.api.chunkAPI.get(chunk);

                            // Verify if the chunk is null or not claimable.
                            // Players should be required to claim the land
                            // they're standing on
                            if (chunkDTO == null || !ChunkAPI.isClaimable(chunkDTO)) {
                                player.sendMessage("The chunk you're standing on is either non-claimable or not found. Maybe try to claim it first?");
                                return;
                            }

                            final String landId = chunkDTO.landId;

                            // Verify if the chunk can expire. This is
                            // required because we're checking for 'expirable'
                            // chunks only.
                            if(!chunkDTO.canExpire()) {
                                player.sendMessage(PluginTextComponent.warning("This thunk is permanent and cannot be extended."));
                                return;
                            }

                            LandDTO landDTO = main.api.landAPI.get(landId);

                            // Verify again if we actually have a land with the chunkId.
                            // This will prevent players from accessing the command and
                            // claiming land that is not theirs.
                            if(landDTO == null) {
                                player.sendMessage("You cannot extend an unclaimed chunk.");
                                return;
                            }

                            // Verify if the chunk is actually owner by the player. You
                            // can only extend your own chunks.
                            if(!UUID.fromString(landDTO.ownerId).equals(playerUuid)) {
                                player.sendMessage("This land is not yours.");
                                return;
                            }

                            // Go through the validation process of the player.
                            ValidateCommand validateCommand = new ValidateCommand.Builder(player)
                                    .checkPrimaryGroup()
                                    .checkRankConfiguration()
                                    .build();

                            if(!validateCommand.passed()) {
                                player.sendMessage("We could not pass the validation of the command.");
                                return;
                            }
                            ValidateCommand.Builder builder = validateCommand.getBuilder();
                            RankConfiguration rankConfiguration = builder.getRankConfiguration();

                            // Make a new payable command.
                            if (!ConfirmCommandManager.isConfirmed(player)) {
                                ConfirmCommandManager.requestCommandConfirmation(player,
                                        new PayableCommand(
                                                "/land extend",
                                                new ArrayList<>(),
                                                2000, // TODO: PricesConfig
                                                30L,
                                                rankConfiguration.getDiscountPercentage()
                                        )
                                );
                                return;
                            }

                            final int dayCount = 7;
                            boolean isExtended = main.api.chunkAPI.extend(chunk, dayCount);

                            if(!isExtended) {
                                player.sendMessage("Oops, Could not extend your land.");
                                return;
                            }

                            player.sendMessage("We've extended your chunk with another " + dayCount + " days. Do keep in mind that only counts for the chunk you're standing on.");

                        })
                )
                .withSubcommand(new CommandAPICommand("debug")
                        .withRequirement(ServerOperator::isOp)
                        .withPermission(CommandPermission.OP)
                        .withSubcommand(new CommandAPICommand("blacklistradius")
                                .executesPlayer((player, args) -> {
                                    Location playerLocation = player.getLocation();
                                    Location center = new Location(player.getWorld(), 0, playerLocation.getY(), 0);
                                    int blockRadius = main.serverConfig.getBlacklistedBlockRadius();
                                    int chunkRadius = main.serverConfig.getBlacklistedChunkRadius();

                                    boolean isInRadiusConfig = main.serverConfig.isWithinBlacklistedChunkRadius(playerLocation);
                                    boolean isInRadiusUtil = LocationUtil.isWithinRadius(center, playerLocation, blockRadius);

                                    player.sendMessage("isInRadiusConfig: " + isInRadiusConfig);
                                    player.sendMessage("isInRadiusUtil: " + isInRadiusUtil);
                                    player.sendMessage(String.format("playerLocation: X=%.2f, Y=%.2f, Z=%.2f", playerLocation.getX(), playerLocation.getY(), playerLocation.getZ()));
                                    player.sendMessage(String.format("center: X=%.2f, Y=%.2f, Z=%.2f", center.getX(), center.getY(), center.getZ()));
                                    player.sendMessage("blockRadius: "+  blockRadius);
                                    player.sendMessage("chunkRadius: "+  chunkRadius);
                                })
                        )
                )
                .register();
    }

}
