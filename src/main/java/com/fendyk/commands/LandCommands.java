package com.fendyk.commands;

import com.fendyk.API;
import com.fendyk.DTOs.*;
import com.fendyk.DTOs.updates.UpdateChunkDTO;
import com.fendyk.DTOs.updates.UpdateLandDTO;
import com.fendyk.Main;
import com.fendyk.clients.apis.ChunkAPI;
import com.fendyk.configs.MessagesConfig;
import com.fendyk.managers.ConfirmCommandManager;
import com.fendyk.managers.WorldguardSyncManager;
import com.fendyk.utilities.*;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
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
import java.util.stream.Collectors;

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

                            // Only continue if we're in the overworld
                            if(!chunk.getWorld().equals(main.getServerConfig().getOverworld())) {
                                player.sendMessage("You can only claim chunks in the overworld.");
                                return;
                            }

                            // Check if the player is within the blacklisted chunk radius
                            if(main.getServerConfig().isWithinBlacklistedChunkRadius(player.getLocation())) {
                                player.sendMessage("The chunk you're currently standing on is considered 'blacklisted' and not claimable.");
                                return;
                            }

                            LandDTO landDTO = main.getApi().getLandAPI().get(uuid);
                            if (landDTO != null) {
                                player.sendMessage("You've already created a land. Lands can only be created once by one player.");
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
                                                "/land create " + name,
                                                new ArrayList<>(),
                                                2000,
                                                30L,
                                                rankConfiguration.getDiscountPercentage()
                                        )
                                );
                                return;
                            }
                            boolean isWithdrawn = main.getApi().getMinecraftUserAPI().withDrawBalance(player, new BigDecimal(2000));
                            if (!isWithdrawn) {
                                player.sendMessage(ChatColor.RED + "Could not withdraw money.");
                                return;
                            }

                            try {
                                LandDTO landDTO1 = api.getLandAPI().create(player, name, chunk, player.getLocation());
                                WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(16, 185, 129, 15));
                                ParticleEffect.FIREWORKS_SPARK.display(player.getLocation());
                                player.sendMessage("Your land has been created");
                                Bukkit.broadcast( Component.text(player.getName() + " has created a new land called '" + landDTO1.getName() + "' somewhere in the universe!", NamedTextColor.GREEN));
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

                                    // Only continue if we're in the overworld
                                    if(!chunk.getWorld().equals(main.getServerConfig().getOverworld())) {
                                        player.sendMessage("You can only claim chunks in the overworld.");
                                        return;
                                    }

                                    // Check if the player is within the blacklisted chunk radius
                                    if(main.getServerConfig().isWithinBlacklistedChunkRadius(player.getLocation())) {
                                        player.sendMessage("The chunk you're currently standing on is considered 'blacklisted' and not claimable.");
                                        return;
                                    }

                                    LandDTO landDTO = api.getLandAPI().getRedis().get(player.getUniqueId().toString());
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

                                    if (chunkDTO.isClaimable() != null && !chunkDTO.isClaimable()) {
                                        WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(252, 211, 77, 15));
                                        player.sendMessage("This chunk is considered not claimable");
                                        return;
                                    }

                                    // Find out if there is a neighbour.
                                    List<Chunk> neighbours = ChunkUtils.getNeighboringChunks(chunk);
                                    long countNeighbours = neighbours.stream().filter(neighbour -> {
                                        ChunkDTO neighbourChunkDTO = api.getChunkAPI().getRedis().get(new Vector2(neighbour.getX(), neighbour.getZ()));
                                        return neighbourChunkDTO != null && neighbourChunkDTO.getLandId() != null && neighbourChunkDTO.getLandId().equals(landDTO.getId());
                                    }).count();

                                    if (countNeighbours < 1) {
                                        player.sendMessage("You can only claim chunks that are neighbours of you current land.");
                                        return;
                                    }

                                    ArrayList<ChunkDTO> chunkDTOS = landDTO.getChunks();

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
                                                        1500,
                                                        30L,
                                                        rankConfiguration.getDiscountPercentage()
                                                )
                                        );
                                        return;
                                    }
                                    boolean isWithdrawn = main.getApi().getMinecraftUserAPI().withDrawBalance(player, new BigDecimal(1500));
                                    if (!isWithdrawn) {
                                        player.sendMessage(ChatColor.RED + "Could not withdraw money.");
                                        return;
                                    }

                                    DateTime expireDate = new DateTime().plusDays(7);
                                    boolean isClaimed = api.getChunkAPI().claim(chunk, landDTO.getId(), true, expireDate);
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
                                    if(!chunk.getWorld().equals(main.getServerConfig().getOverworld())) {
                                        player.sendMessage("You can only claim chunks in the overworld.");
                                        return;
                                    }

                                    // Check if the player is within the blacklisted chunk radius
                                    if(main.getServerConfig().isWithinBlacklistedChunkRadius(player.getLocation())) {
                                        player.sendMessage("The chunk you're currently standing on is considered 'blacklisted' and not claimable.");
                                        return;
                                    }

                                    LandDTO landDTO = api.getLandAPI().getRedis().get(player.getUniqueId().toString());
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

                                    if (chunkDTO.isClaimable() != null && !chunkDTO.isClaimable()) {
                                        WorldguardSyncManager.showParticleEffectAtChunk(chunk, player.getLocation(), new DustData(252, 211, 77, 15));
                                        player.sendMessage("This chunk is considered not claimable");
                                        return;
                                    }

                                    // Find out if there is a neighbour.
                                    List<Chunk> neighbours = ChunkUtils.getNeighboringChunks(chunk);
                                    long countNeighbours = neighbours.stream().filter(neighbour -> {
                                        ChunkDTO neighbourChunkDTO = api.getChunkAPI().getRedis().get(new Vector2(neighbour.getX(), neighbour.getZ()));
                                        return neighbourChunkDTO != null && neighbourChunkDTO.getLandId() != null && neighbourChunkDTO.getLandId().equals(landDTO.getId());
                                    }).count();

                                    if (countNeighbours < 1) {
                                        player.sendMessage("You can only claim chunks that are neighbours of you current land.");
                                        return;
                                    }

                                    ArrayList<ChunkDTO> chunkDTOS = landDTO.getChunks();

                                    if(chunkDTOS != null && chunkDTOS.size() >= rankConfiguration.getChunkSlots()) {
                                        player.sendMessage("You've reached your limit for claiming new permanent chunks.");
                                        return;
                                    }

                                    if (!ConfirmCommandManager.isConfirmed(player)) {
                                        ConfirmCommandManager.requestCommandConfirmation(player,
                                                new PayableCommand(
                                                        "/land claim permanent",
                                                        new ArrayList<>(),
                                                        1500,
                                                        30L,
                                                        rankConfiguration.getDiscountPercentage()
                                                )
                                        );
                                        return;
                                    }
                                    boolean isWithdrawn = main.getApi().getMinecraftUserAPI().withDrawBalance(player, new BigDecimal(1500));
                                    if (!isWithdrawn) {
                                        player.sendMessage(ChatColor.RED + "Could not withdraw money.");
                                        return;
                                    }

                                    boolean isClaimed = api.getChunkAPI().claim(chunk, landDTO.getId(), false, null);
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
                            World world = main.getServerConfig().getOverworld();

                            LandDTO landDTO = api.getLandAPI().getRedis().get(uuid.toString());
                            if (landDTO == null) {
                                player.sendMessage("Could not find your land");
                                return;
                            }

                            Optional<TaggedLocationDTO> taggedLocationDTO = Optional.ofNullable(landDTO.getHomes().stream()
                                    .filter(t -> t.getName().equals("spawn"))
                                    .findAny()
                                    .orElseGet(() -> landDTO.getHomes().stream().findFirst().orElse(null)));

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
                                                2,
                                                30L,
                                                rankConfiguration.getDiscountPercentage()
                                        )
                                );
                                return;
                            }
                            boolean isWithdrawn = main.getApi().getMinecraftUserAPI().withDrawBalance(player, new BigDecimal(2));
                            if (!isWithdrawn) {
                                player.sendMessage(ChatColor.RED + "Could not withdraw money.");
                                return;
                            }

                            LocationDTO loc = taggedLocationDTO.get().getLocation();

                            player.teleport(new Location(world, loc.getX(), loc.getY(), loc.getZ(), (float) loc.getYaw(), (float) loc.getPitch()));
                            player.sendMessage(player.getName() + " You have been teleported to your spawn");
                        })
                )
                // ### /land info ###
                .withSubcommand(new CommandAPICommand("info")
                        .executesPlayer((player, args) -> {
                            Chunk chunk = player.getChunk();

                            // Only continue if we're in the overworld
                            if(!chunk.getWorld().equals(main.getServerConfig().getOverworld())) {
                                player.sendMessage("You can only see information about a chunk in the overworld");
                                return;
                            }

                            // Check if the player is within the blacklisted chunk radius
                            if(main.getServerConfig().isWithinBlacklistedChunkRadius(player.getLocation())) {
                                player.sendMessage("The chunk you're currently standing on is considered 'blacklisted' and not claimable.");
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
                        .withSubcommand(new CommandAPICommand("tp")
                                .withArguments(new StringArgument("name"))
                                .executesPlayer((player, args) -> {
                                    String name = (String) args[0];
                                    UUID uuid = player.getUniqueId();
                                    World world = main.getServerConfig().getOverworld();

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
                                                        2,
                                                        30L,
                                                        rankConfiguration.getDiscountPercentage()
                                                )
                                        );
                                        return;
                                    }
                                    boolean isWithdrawn = main.getApi().getMinecraftUserAPI().withDrawBalance(player, new BigDecimal(2));
                                    if (!isWithdrawn) {
                                        player.sendMessage(ChatColor.RED + "Could not withdraw money.");
                                        return;
                                    }

                                    LocationDTO loc = taggedLocationDTO.get().getLocation();

                                    player.teleport(new Location(world, loc.getX(), loc.getY(), loc.getZ(), (float) loc.getYaw(), (float) loc.getPitch()));
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
                                    if(!chunk.getWorld().equals(main.getServerConfig().getOverworld())) {
                                        player.sendMessage("You can only claim chunks in the overworld.");
                                        return;
                                    }

                                    UpdateLandDTO updateLandDTO = new UpdateLandDTO();
                                    LandDTO landDTO = api.getLandAPI().getRedis().get(uuid.toString());
                                    if (landDTO == null) {
                                        player.sendMessage("Could not find your land");
                                        return;
                                    } else if (landDTO.getHomes() != null && landDTO.getHomes().stream().filter(taggedLocationDTO -> taggedLocationDTO.getName().equals(name)).count() > 1) {
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

                                    ArrayList<TaggedLocationDTO> taggedLocationDTOS = landDTO.getHomes();
                                    if(taggedLocationDTOS != null && taggedLocationDTOS.size() >= rankConfiguration.getHomeSlots()) {
                                        player.sendMessage("You've reached your limit for setting new land homes.");
                                        return;
                                    }

                                    if (!ConfirmCommandManager.isConfirmed(player)) {
                                        ConfirmCommandManager.requestCommandConfirmation(player,
                                                new PayableCommand(
                                                        "/land home set " + name,
                                                        new ArrayList<>(),
                                                        250,
                                                        30L,
                                                        rankConfiguration.getDiscountPercentage()
                                                )
                                        );
                                        return;
                                    }
                                    boolean isWithdrawn = main.getApi().getMinecraftUserAPI().withDrawBalance(player, new BigDecimal(250));
                                    if (!isWithdrawn) {
                                        player.sendMessage(ChatColor.RED + "Could not withdraw money.");
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
                                .withArguments(new PlayerArgument("name"))
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

                                    ArrayList<String> memberIDs = landDTO.getMemberIDs();
                                    if(memberIDs != null && memberIDs.size() >= rankConfiguration.getMemberSlots()) {
                                        player.sendMessage("You've reached your limit for setting new land homes.");
                                        return;
                                    }

                                    if (!ConfirmCommandManager.isConfirmed(player)) {
                                        ConfirmCommandManager.requestCommandConfirmation(player,
                                                new PayableCommand(
                                                        "/land member add " + newMember.getName(),
                                                        new ArrayList<>(),
                                                        750,
                                                        30L,
                                                        rankConfiguration.getDiscountPercentage()
                                                )
                                        );
                                        return;
                                    }
                                    boolean isWithdrawn = main.getApi().getMinecraftUserAPI().withDrawBalance(player, new BigDecimal(750));
                                    if (!isWithdrawn) {
                                        player.sendMessage(ChatColor.RED + "Could not withdraw money.");
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
                                .withArguments(new PlayerArgument("name"))
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
                        .withRequirement(ServerOperator::isOp)
                        .withPermission(CommandPermission.OP)
                        .withArguments(new BooleanArgument("isClaimable"))
                        .executesPlayer((player, args) -> {
                            boolean isClaimable = (boolean) args[0];
                            Chunk chunk = player.getChunk();

                            // Only continue if we're in the overworld
                            if(!chunk.getWorld().equals(main.getServerConfig().getOverworld())) {
                                player.sendMessage("You can only generate chunks in the overworld.");
                                return;
                            }


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

                            // Only continue if we're in the overworld
                            if(!chunk.getWorld().equals(main.getServerConfig().getOverworld())) {
                                player.sendMessage("You can only extend chunks in the overworld.");
                                return;
                            }

                            // Check if the player is within the blacklisted chunk radius
                            if(main.getServerConfig().isWithinBlacklistedChunkRadius(player.getLocation())) {
                                player.sendMessage("The chunk you're currently standing on is considered 'blacklisted' and not claimable.");
                                return;
                            }

                            ChunkDTO chunkDTO = main.getApi().getChunkAPI().get(chunk);
                            if (chunkDTO == null || !ChunkAPI.isClaimable(chunkDTO)) {
                                player.sendMessage(main.getMessagesConfig().getMessage(MessagesConfig.State.CHUNK_IS_NOT_CLAIMABLE));
                                return;
                            }

                            if(!chunkDTO.canExpire()) {
                                player.sendMessage("This chunk is permanent and cannot be extended. Hooray ;)");
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
                                                "/land extend",
                                                new ArrayList<>(),
                                                2000,
                                                30L,
                                                rankConfiguration.getDiscountPercentage()
                                        )
                                );
                                return;
                            }
                            boolean isWithdrawn = main.getApi().getMinecraftUserAPI().withDrawBalance(player, new BigDecimal(2000));
                            if (!isWithdrawn) {
                                player.sendMessage(ChatColor.RED + "Could not withdraw money.");
                                return;
                            }

                            boolean isExtended = main.getApi().getChunkAPI().extend(chunk, 7);

                            if(!isExtended) {
                                player.sendMessage("Oops, Could not extend your land.");
                                return;
                            }

                            player.sendMessage("We've extended your chunk with another 7 days. Do keep in mind that only counts for the chunk you're standing on.");

                        })
                )
                .withSubcommand(new CommandAPICommand("debug")
                        .withRequirement(ServerOperator::isOp)
                        .withPermission(CommandPermission.OP)
                        .withSubcommand(new CommandAPICommand("blacklistradius")
                                .executesPlayer((player, args) -> {
                                    Location playerLocation = player.getLocation();
                                    Location center = new Location(player.getWorld(), 0, playerLocation.getY(), 0);
                                    int blockRadius = main.getServerConfig().getBlacklistedBlockRadius();
                                    int chunkRadius = main.getServerConfig().getBlacklistedChunkRadius();

                                    boolean isInRadiusConfig = main.getServerConfig().isWithinBlacklistedChunkRadius(playerLocation);
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
