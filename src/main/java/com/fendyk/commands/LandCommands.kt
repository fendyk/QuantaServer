package com.fendyk.commands

import com.fendyk.DTOs.ChunkDTO
import com.fendyk.DTOs.LandDTO
import com.fendyk.DTOs.LocationDTO
import com.fendyk.DTOs.TaggedLocationDTO
import com.fendyk.DTOs.updates.UpdateLandDTO
import com.fendyk.DTOs.updates.UpdateLandDTO.MemberDTO
import com.fendyk.Main
import com.fendyk.clients.apis.ChunkAPI
import com.fendyk.managers.ConfirmCommandManager
import com.fendyk.managers.WorldguardSyncManager
import com.fendyk.utilities.*
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.BooleanArgument
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.joda.time.DateTime
import xyz.xenondevs.particle.ParticleEffect
import xyz.xenondevs.particle.data.color.DustData
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier
import java.util.stream.Collectors

class LandCommands {
    var main = Main.instance

    init {
        val api = main.api
        val serverConfig = main.serverConfig
        val landAPI = api.landAPI
        val chunkAPI = api.chunkAPI
        val minecraftUserAPI = api.minecraftUserAPI
        val overworld = serverConfig.overworld

        CommandAPICommand("land") // ### /land ###
            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?>? ->
                val uuid = player.uniqueId
                val futLand = landAPI.get(uuid)
                futLand.handleAsync{ t, u ->
                    if(u != null) {
                        player.sendMessage(
                            PluginTextComponent.warning(
                                "You have not created a land yet. To create one," +
                                        " type /land create <name>"
                            )
                        )
                        return@handleAsync
                    }

                    player.sendMessage(PluginTextComponent.statistic("ID:", t.id))
                    player.sendMessage(PluginTextComponent.statistic("Name:", t.name))
                    player.sendMessage(PluginTextComponent.statistic("Member count:", t.memberIDs.size.toString()))
                    player.sendMessage(PluginTextComponent.statistic("Home count:", t.homes.size.toString()))

                }
            }) // ### /land help ###
            .withSubcommand(
                CommandAPICommand("help")
                    .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?>? ->
                        player.sendMessage("/land - To view information about your land")
                        player.sendMessage("/land info - To view information about the land you're standing on")
                        player.sendMessage("/land create <name> - To create your first land")
                        player.sendMessage("/land claim - To claim a chunk you're standing on")
                        player.sendMessage("/land spawn - To visit your land's spawn")
                        player.sendMessage("/land homes - To view all your homes")
                        player.sendMessage("/land home <name> - To visit your home")
                        player.sendMessage("/land home set,remove,tp <name> - To set, remove or teleport to a home")
                    })
            ) // ### /land create <name> ###
            .withSubcommand(
                CommandAPICommand("create")
                    .withArguments(StringArgument("name"))
                    .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any> ->
                        val name = args[0] as String
                        val chunk = player.chunk
                        val uuid = player.uniqueId

                        // Only continue if we're in the OverWorld
                        if (chunk.world != overworld) {
                            player.sendMessage(
                                PluginTextComponent.error(
                                    "You can only create land in the overworld"
                                )
                            )
                            return@executesPlayer
                        }

                        // Check if the player is within the blacklisted chunk radius
                        if (serverConfig.isWithinBlacklistedChunkRadius(player.location)) {
                            player.sendMessage(
                                PluginTextComponent.warning(
                                    "The chunk you're currently standing on is considered " +
                                            "'blacklisted' and not claimable."
                                )
                            )
                            return@executesPlayer
                        }
                        val landDTO: LandDTO = landAPI.get(uuid)
                        if (landDTO != null) {
                            player.sendMessage(
                                PluginTextComponent.warning(
                                    "You've already created a land. Lands can only be created once by one player."
                                )
                            )
                            return@executesPlayer
                        }
                        val validateCommand = ValidateCommand.Builder(player)
                            .checkPrimaryGroup()
                            .checkRankConfiguration()
                            .build()
                        if (!validateCommand.passed()) {
                            player.sendMessage(
                                PluginTextComponent.error(
                                    "We could not pass the validation of the command."
                                )
                            )
                            return@executesPlayer
                        }
                        val builder = validateCommand.builder
                        val rankConfiguration = builder.rankConfiguration
                        if (!ConfirmCommandManager.isConfirmed(player)) {
                            ConfirmCommandManager.requestCommandConfirmation(
                                player,
                                PayableCommand(
                                    "/land create $name",
                                    java.util.ArrayList(),
                                    main.pricesConfig.landCreatePrice,
                                    60L,
                                    rankConfiguration.discountPercentage
                                )
                            )
                            return@executesPlayer
                        }
                        try {
                            val (_, name1) = landAPI.create(player, name, chunk, player.location)
                            WorldguardSyncManager.showParticleEffectAtChunk(
                                chunk,
                                player.location,
                                DustData(16, 185, 129, 15f)
                            )
                            ParticleEffect.FIREWORKS_SPARK.display(player.location)
                            player.sendMessage("Your land has been created")
                            Bukkit.broadcast(
                                Component.text(
                                    player.name + " has created a new land called '" + name1 + "' somewhere in the universe!",
                                    NamedTextColor.GREEN
                                )
                            )
                        } catch (e: Exception) {
                            Bukkit.getLogger().info(Arrays.toString(e.stackTrace))
                            player.sendMessage(e.message!!)
                        }
                    })
            ) // ### /claim ###
            .withSubcommand(
                CommandAPICommand("claim")
                    .withSubcommand(
                        CommandAPICommand("expirable")
                            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?>? ->
                                val chunk = player.chunk
                                val uuid = player.uniqueId

                                // Only continue if we're in the OverWorld
                                if (chunk.world != overworld) {
                                    player.sendMessage(
                                        PluginTextComponent.error(
                                            "You can only create land in the overworld"
                                        )
                                    )
                                    return@executesPlayer
                                }

                                // Check if the player is within the blacklisted chunk radius
                                if (serverConfig.isWithinBlacklistedChunkRadius(player.location)) {
                                    player.sendMessage(
                                        PluginTextComponent.warning(
                                            "The chunk you're currently standing on is considered " +
                                                    "'blacklisted' and not claimable."
                                        )
                                    )
                                    return@executesPlayer
                                }
                                val landDTO: LandDTO = api.landAPI.redis.get(player.uniqueId.toString())
                                if (landDTO == null) {
                                    player.sendMessage(
                                        PluginTextComponent.warning(
                                            "You currently dont have a land. To create one," +
                                                    " type /land create <name>"
                                        )
                                    )
                                    return@executesPlayer
                                }
                                val validateCommand = ValidateCommand.Builder(player)
                                    .checkPrimaryGroup()
                                    .checkRankConfiguration()
                                    .checkMinecraftUserDTO()
                                    .build()
                                if (!validateCommand.passed()) {
                                    player.sendMessage("We could not pass the validation of the command.")
                                    return@executesPlayer
                                }
                                val builder = validateCommand.builder
                                val rankConfiguration = builder.rankConfiguration
                                val (id) = builder.minecraftUserDTO
                                var chunkDTO: ChunkDTO = api.chunkAPI.redis.get(Vector2(chunk.x, chunk.z))
                                if (chunkDTO == null) {
                                    chunkDTO = api.chunkAPI.create(chunk, true)
                                    if (chunkDTO == null) {
                                        player.sendMessage("Chunk could not be found.")
                                        return@executesPlayer
                                    }
                                }
                                val chunkLandId = chunkDTO.landId
                                if (chunkLandId != null) {
                                    WorldguardSyncManager.showParticleEffectAtChunk(
                                        chunk,
                                        player.location,
                                        DustData(239, 68, 68, 15f)
                                    )
                                    player.sendMessage("This chunk has already been claimed by someone else")
                                    return@executesPlayer
                                }
                                if (chunkDTO.isClaimable != null && !chunkDTO.isClaimable!!) {
                                    WorldguardSyncManager.showParticleEffectAtChunk(
                                        chunk,
                                        player.location,
                                        DustData(252, 211, 77, 15f)
                                    )
                                    player.sendMessage("This chunk is considered not claimable")
                                    return@executesPlayer
                                }

                                // Find out if there is a neighbour.
                                val neighbours = ChunkUtils.getNeighboringChunks(chunk)
                                val countNeighbours = neighbours.stream().filter { neighbour: Chunk ->
                                    val neighbourChunkDTO: ChunkDTO =
                                        api.chunkAPI.redis.get(Vector2(neighbour.x, neighbour.z))
                                    neighbourChunkDTO != null && neighbourChunkDTO.landId != null && neighbourChunkDTO.landId == landDTO.id
                                }.count()
                                if (countNeighbours < 1) {
                                    player.sendMessage("You can only claim chunks that are neighbours of you current land.")
                                    return@executesPlayer
                                }
                                val chunkDTOS = landDTO.chunks
                                if (chunkDTOS != null) {
                                    val expirableChunkDTOS = chunkDTOS.stream()
                                        .filter { obj: ChunkDTO -> obj.canExpire()!! }
                                        .collect(
                                            Collectors.toCollection(
                                                Supplier { ArrayList() })
                                        )
                                    if (expirableChunkDTOS.size >= rankConfiguration.renewableChunkSlots) {
                                        player.sendMessage("You've reached your limit for claiming new renewable chunks.")
                                        return@executesPlayer
                                    }
                                }
                                if (!ConfirmCommandManager.isConfirmed(player)) {
                                    ConfirmCommandManager.requestCommandConfirmation(
                                        player,
                                        PayableCommand(
                                            "/land claim expirable",
                                            java.util.ArrayList(),
                                            main.pricesConfig.landClaimExpirablePrice,
                                            30L,
                                            rankConfiguration.discountPercentage
                                        )
                                    )
                                    return@executesPlayer
                                }
                                val expireDate = DateTime().plusDays(7)
                                val isClaimed: Boolean = api.chunkAPI.claim(chunk, landDTO.id, true, expireDate)
                                if (!isClaimed) {
                                    player.sendMessage("Could not claim chunk.")
                                    return@executesPlayer
                                }
                                WorldguardSyncManager.showParticleEffectAtChunk(
                                    chunk,
                                    player.location,
                                    DustData(16, 185, 129, 15f)
                                )
                                ParticleEffect.FIREWORKS_SPARK.display(player.location)
                                player.sendMessage("Chunk has been claimed.")
                                player.sendMessage("This is not permanent and will expire at $expireDate")
                                Bukkit.broadcast(
                                    Component.text(
                                        player.name + " has claimed a renewable chunk somewhere in the universe!",
                                        NamedTextColor.GREEN
                                    )
                                )
                            })
                    )
                    .withSubcommand(
                        CommandAPICommand("permanent")
                            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?>? ->
                                val chunk = player.chunk
                                val uuid = player.uniqueId

                                // Only continue if we're in the overworld
                                if (chunk.world != main.serverConfig.overworld) {
                                    player.sendMessage("You can only claim chunks in the overworld.")
                                    return@executesPlayer
                                }

                                // Check if the player is within the blacklisted chunk radius
                                if (main.serverConfig.isWithinBlacklistedChunkRadius(player.location)) {
                                    player.sendMessage("The chunk you're currently standing on is considered 'blacklisted' and not claimable.")
                                    return@executesPlayer
                                }
                                val landDTO: LandDTO = api.landAPI.redis.get(player.uniqueId.toString())
                                if (landDTO == null) {
                                    player.sendMessage("You currently dont have a land. To create one, type /land create <name>")
                                    return@executesPlayer
                                }
                                val validateCommand = ValidateCommand.Builder(player)
                                    .checkPrimaryGroup()
                                    .checkRankConfiguration()
                                    .checkMinecraftUserDTO()
                                    .build()
                                if (!validateCommand.passed()) {
                                    player.sendMessage("We could not pass the validation of the command.")
                                    return@executesPlayer
                                }
                                val builder = validateCommand.builder
                                val rankConfiguration = builder.rankConfiguration
                                val (id) = builder.minecraftUserDTO
                                var chunkDTO: ChunkDTO = api.chunkAPI.redis.get(Vector2(chunk.x, chunk.z))
                                if (chunkDTO == null) {
                                    chunkDTO = api.chunkAPI.create(chunk, true)
                                    if (chunkDTO == null) {
                                        player.sendMessage("Chunk could not be found.")
                                        return@executesPlayer
                                    }
                                }
                                val chunkLandId = chunkDTO.landId
                                if (chunkLandId != null) {
                                    WorldguardSyncManager.showParticleEffectAtChunk(
                                        chunk,
                                        player.location,
                                        DustData(239, 68, 68, 15f)
                                    )
                                    player.sendMessage("This chunk has already been claimed by someone else")
                                    return@executesPlayer
                                }
                                if (chunkDTO.isClaimable != null && !chunkDTO.isClaimable!!) {
                                    WorldguardSyncManager.showParticleEffectAtChunk(
                                        chunk,
                                        player.location,
                                        DustData(252, 211, 77, 15f)
                                    )
                                    player.sendMessage("This chunk is considered not claimable")
                                    return@executesPlayer
                                }

                                // Find out if there is a neighbour.
                                val neighbours = ChunkUtils.getNeighboringChunks(chunk)
                                val countNeighbours = neighbours.stream().filter { neighbour: Chunk ->
                                    val neighbourChunkDTO: ChunkDTO =
                                        api.chunkAPI.redis.get(Vector2(neighbour.x, neighbour.z))
                                    neighbourChunkDTO != null && neighbourChunkDTO.landId != null && neighbourChunkDTO.landId == landDTO.id
                                }.count()
                                if (countNeighbours < 1) {
                                    player.sendMessage("You can only claim chunks that are neighbours of you current land.")
                                    return@executesPlayer
                                }
                                val chunkDTOS = landDTO.chunks
                                if (chunkDTOS != null && chunkDTOS.size >= rankConfiguration.chunkSlots) {
                                    player.sendMessage("You've reached your limit for claiming new permanent chunks.")
                                    return@executesPlayer
                                }
                                if (!ConfirmCommandManager.isConfirmed(player)) {
                                    ConfirmCommandManager.requestCommandConfirmation(
                                        player,
                                        PayableCommand(
                                            "/land claim permanent",
                                            java.util.ArrayList(),
                                            main.pricesConfig.landClaimPermanentPrice,
                                            30L,
                                            rankConfiguration.discountPercentage
                                        )
                                    )
                                    return@executesPlayer
                                }
                                val isClaimed: Boolean = api.chunkAPI.claim(chunk, landDTO.id, false, null)
                                if (!isClaimed) {
                                    player.sendMessage("Could not claim chunk.")
                                    return@executesPlayer
                                }
                                WorldguardSyncManager.showParticleEffectAtChunk(
                                    chunk,
                                    player.location,
                                    DustData(16, 185, 129, 15f)
                                )
                                ParticleEffect.FIREWORKS_SPARK.display(player.location)
                                player.sendMessage("Chunk has been claimed.")
                                Bukkit.broadcast(
                                    Component.text(
                                        player.name + " has claimed a permanent chunk somewhere in the universe!",
                                        NamedTextColor.GREEN
                                    )
                                )
                            })
                    )
            ) // ### /land spawn ###
            .withSubcommand(
                CommandAPICommand("spawn")
                    .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?>? ->
                        val uuid = player.uniqueId
                        val world = main.serverConfig.overworld
                        val landDTO: LandDTO = api.landAPI.redis.get(uuid.toString())
                        if (landDTO == null) {
                            player.sendMessage("Could not find your land")
                            return@executesPlayer
                        }
                        val taggedLocationDTO = Optional.ofNullable(landDTO.homes.stream()
                            .filter { (name): TaggedLocationDTO -> name == "spawn" }
                            .findAny()
                            .orElseGet { landDTO.homes.stream().findFirst().orElse(null) })
                        if (taggedLocationDTO.isEmpty) {
                            player.sendMessage("Could not locate your spawn location. Try adding a home naming 'spawn'")
                            return@executesPlayer
                        }
                        val validateCommand = ValidateCommand.Builder(player)
                            .checkPrimaryGroup()
                            .checkRankConfiguration()
                            .build()
                        if (!validateCommand.passed()) {
                            player.sendMessage("We could not pass the validation of the command.")
                            return@executesPlayer
                        }
                        val builder = validateCommand.builder
                        val rankConfiguration = builder.rankConfiguration
                        if (!ConfirmCommandManager.isConfirmed(player)) {
                            ConfirmCommandManager.requestCommandConfirmation(
                                player,
                                PayableCommand(
                                    "/land spawn",
                                    java.util.ArrayList(),
                                    main.pricesConfig.teleportCommandPrice,
                                    30L,
                                    rankConfiguration.discountPercentage
                                )
                            )
                            return@executesPlayer
                        }
                        val (x, y, z, yaw, pitch) = taggedLocationDTO.get().location
                        player.teleport(Location(world, x, y, z, yaw as Float, pitch as Float))
                        player.sendMessage(player.name + " You have been teleported to your spawn")
                    })
            ) // ### /land info ###
            .withSubcommand(
                CommandAPICommand("info")
                    .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?>? ->
                        val chunk = player.chunk

                        // Only continue if we're in the overworld
                        if (chunk.world != main.serverConfig.overworld) {
                            player.sendMessage("You can only see information about a chunk in the overworld")
                            return@executesPlayer
                        }

                        // Check if the player is within the blacklisted chunk radius
                        if (main.serverConfig.isWithinBlacklistedChunkRadius(player.location)) {
                            player.sendMessage("The chunk you're currently standing on is considered 'blacklisted' and not claimable.")
                            return@executesPlayer
                        }
                        val chunkDTO: ChunkDTO = api.chunkAPI.redis.get(Vector2(chunk.x, chunk.z))
                        if (chunkDTO == null || chunkDTO.landId == null) {
                            WorldguardSyncManager.showParticleEffectAtChunk(
                                chunk,
                                player.location,
                                DustData(16, 185, 129, 15f)
                            )
                            player.sendMessage("This chunk has not been claimed yet.")
                            return@executesPlayer
                        }
                        if (chunkDTO.isClaimable != null && !chunkDTO.isClaimable!!) {
                            WorldguardSyncManager.showParticleEffectAtChunk(
                                chunk,
                                player.location,
                                DustData(252, 211, 77, 15f)
                            )
                            player.sendMessage("This chunk is considered not claimable")
                            return@executesPlayer
                        }
                        val landDTO: LandDTO = api.landAPI.redis.get(chunkDTO.landId!!)
                        if (landDTO == null) {
                            player.sendMessage("Could not find land at current chunk.")
                            return@executesPlayer
                        }
                        val awaitMinecraftUser = minecraftUserAPI.get(UUID.fromString(landDTO.ownerId))
                        // Add more here

                        // Perform actions
                        CompletableFuture.allOf(awaitMinecraftUser).handleAsync<Any?> { unused: Void?, ex: Throwable? ->
                            if (ex != null) {
                                player.sendMessage(ex.message!!)
                                return@handleAsync null
                            }
                            val (id) = awaitMinecraftUser.join()
                            null
                        }
                        if (landDTO.ownerId != player.uniqueId.toString()) {
                            WorldguardSyncManager.showParticleEffectAtChunk(
                                chunk,
                                player.location,
                                DustData(239, 68, 68, 15f)
                            )
                        } else {
                            WorldguardSyncManager.showParticleEffectAtChunk(
                                chunk,
                                player.location,
                                DustData(59, 130, 246, 15f)
                            )
                        }
                        player.sendMessage("You're currently standing at:")
                        player.sendMessage("Chunk: " + chunkDTO.x + "/" + chunkDTO.z)
                        player.sendMessage("Land:" + landDTO.name)
                        player.sendMessage(
                            "Owned by player: " + Objects.requireNonNull<Player?>(
                                Bukkit.getPlayer(
                                    UUID.fromString(
                                        minecraftUserDTO.getId()
                                    )
                                )
                            ).name
                        )
                        if (chunkDTO.canExpire()!!) {
                            player.sendMessage("Expiration date:" + chunkDTO.getExpirationDate())
                        }
                    })
            ) /* HOMES COMMAND */
            .withSubcommand(
                CommandAPICommand("homes")
                    .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?>? ->
                        val uuid = player.uniqueId
                        val landDTO: LandDTO = api.landAPI.redis.get(uuid.toString())
                        if (landDTO == null) {
                            player.sendMessage("Could not find your land")
                            return@executesPlayer
                        }
                        if (landDTO.homes == null || landDTO.homes.isEmpty()) {
                            player.sendMessage("Could not find any homes yet. Maybe it's time to add one? Type /land homes add <name>")
                            return@executesPlayer
                        }
                        for ((name, loc) in landDTO.homes) {
                            player.sendMessage(name + ": (" + loc.x + "," + loc.y + "," + loc.z + ")")
                        }
                    })
            ) // ### /land home <name> ###
            .withSubcommand(
                CommandAPICommand("home")
                    .withSubcommand(
                        CommandAPICommand("tp")
                            .withArguments(StringArgument("name"))
                            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any> ->
                                val name = args[0] as String
                                val uuid = player.uniqueId
                                val world = main.serverConfig.overworld
                                val landDTO: LandDTO = api.landAPI.redis.get(uuid.toString())
                                if (landDTO == null) {
                                    player.sendMessage("Could not find your land")
                                    return@executesPlayer
                                }
                                val taggedLocationDTO =
                                    landDTO.homes.stream().filter { (name1): TaggedLocationDTO -> name1 == name }
                                        .findFirst()
                                if (taggedLocationDTO.isEmpty) {
                                    player.sendMessage("Could not find any home matching the name: $name")
                                    return@executesPlayer
                                }
                                val validateCommand = ValidateCommand.Builder(player)
                                    .checkPrimaryGroup()
                                    .checkRankConfiguration()
                                    .build()
                                if (!validateCommand.passed()) {
                                    player.sendMessage("We could not pass the validation of the command.")
                                    return@executesPlayer
                                }
                                val builder = validateCommand.builder
                                val rankConfiguration = builder.rankConfiguration
                                if (!ConfirmCommandManager.isConfirmed(player)) {
                                    ConfirmCommandManager.requestCommandConfirmation(
                                        player,
                                        PayableCommand(
                                            "/land home tp $name",
                                            java.util.ArrayList(),
                                            main.pricesConfig.teleportCommandPrice,
                                            30L,
                                            rankConfiguration.discountPercentage
                                        )
                                    )
                                    return@executesPlayer
                                }
                                val (x, y, z, yaw, pitch) = taggedLocationDTO.get().location
                                player.teleport(Location(world, x, y, z, yaw as Float, pitch as Float))
                                player.sendMessage(player.name + " You have been teleported to " + name)
                            })
                    ) // ### /land home set <name> ###
                    .withSubcommand(
                        CommandAPICommand("set")
                            .withArguments(StringArgument("name"))
                            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any> ->
                                val name = args[0] as String
                                val uuid = player.uniqueId
                                val chunk = player.chunk

                                // Only continue if we're in the overworld
                                if (chunk.world != main.serverConfig.overworld) {
                                    player.sendMessage("You can only claim chunks in the overworld.")
                                    return@executesPlayer
                                }
                                val updateLandDTO = UpdateLandDTO()
                                val landDTO: LandDTO = api.landAPI.redis.get(uuid.toString())
                                if (landDTO == null) {
                                    player.sendMessage("Could not find your land")
                                    return@executesPlayer
                                } else if (landDTO.homes != null && landDTO.homes.stream()
                                        .filter { (name1): TaggedLocationDTO -> name1 == name }
                                        .count() > 1
                                ) {
                                    player.sendMessage("You've already set a home with the name $name")
                                    return@executesPlayer
                                }
                                val validateCommand = ValidateCommand.Builder(player)
                                    .checkPrimaryGroup()
                                    .checkRankConfiguration()
                                    .build()
                                if (!validateCommand.passed()) {
                                    player.sendMessage("We could not pass the validation of the command.")
                                    return@executesPlayer
                                }
                                val builder = validateCommand.builder
                                val rankConfiguration = builder.rankConfiguration
                                val taggedLocationDTOS = landDTO.homes
                                if (taggedLocationDTOS != null && taggedLocationDTOS.size >= rankConfiguration.homeSlots) {
                                    player.sendMessage("You've reached your limit for setting new land homes.")
                                    return@executesPlayer
                                }
                                if (!ConfirmCommandManager.isConfirmed(player)) {
                                    ConfirmCommandManager.requestCommandConfirmation(
                                        player,
                                        PayableCommand(
                                            "/land home set $name",
                                            java.util.ArrayList(),
                                            main.pricesConfig.landHomeSetPrice,
                                            30L,
                                            rankConfiguration.discountPercentage
                                        )
                                    )
                                    return@executesPlayer
                                }
                                val locationDTO = LocationDTO(player.location)
                                val taggedLocationDTO = TaggedLocationDTO()
                                taggedLocationDTO.name = name
                                taggedLocationDTO.location = locationDTO
                                updateLandDTO.pushHomes.add(taggedLocationDTO)
                                val result: LandDTO = api.landAPI.fetch.update(landDTO.id, updateLandDTO)
                                if (result == null) {
                                    player.sendMessage("Could not update your land's home.")
                                    return@executesPlayer
                                }
                                player.sendMessage("$name has been added to your land's homes")
                            })
                    ) // ### /land home remove <name> ###
                    .withSubcommand(
                        CommandAPICommand("remove")
                            .withArguments(StringArgument("name"))
                            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any> ->
                                val name = args[0] as String
                                val uuid = player.uniqueId
                                val updateLandDTO = UpdateLandDTO()
                                val landDTO: LandDTO = api.landAPI.redis.get(uuid.toString())
                                if (landDTO == null) {
                                    player.sendMessage("Could not find your land")
                                    return@executesPlayer
                                } else if (landDTO.homes != null && landDTO.homes.stream()
                                        .noneMatch { (name1): TaggedLocationDTO -> name1 == name }
                                ) {
                                    player.sendMessage("Could not find a home with the name: $name")
                                    return@executesPlayer
                                }
                                updateLandDTO.spliceHomes.add(name)
                                val result: LandDTO = api.landAPI.fetch.update(landDTO.id, updateLandDTO)
                                if (result == null) {
                                    player.sendMessage("Could not update your land's home.")
                                    return@executesPlayer
                                }
                                player.sendMessage("$name has been removed from your land's homes")
                            })
                    )
            ) // ### /land members ###
            .withSubcommand(
                CommandAPICommand("members")
                    .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?>? ->
                        val uuid = player.uniqueId
                        val landDTO: LandDTO = api.landAPI.redis.get(uuid.toString())
                        if (landDTO == null) {
                            player.sendMessage("Could not find your land")
                            return@executesPlayer
                        }
                        if (landDTO.memberIDs == null || landDTO.memberIDs.isEmpty()) {
                            player.sendMessage("Could not find any homes yet. Maybe it's time to add one? Type /land homes set")
                            return@executesPlayer
                        }
                        for (member in landDTO.memberIDs) {
                            player.sendMessage(Bukkit.getOfflinePlayer(UUID.fromString(member)).name + " is a member of your land")
                        }
                    })
            ) // ### /land member ###
            .withSubcommand(
                CommandAPICommand("member") // ### /land member add <name> ###
                    .withSubcommand(
                        CommandAPICommand("add")
                            .withArguments(OfflinePlayerArgument("name"))
                            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any> ->
                                val newMember = args[0] as OfflinePlayer
                                val uuid = player.uniqueId
                                val memberUuid = newMember.uniqueId
                                val updateLandDTO = UpdateLandDTO()
                                val landDTO: LandDTO = api.landAPI.redis.get(uuid.toString())
                                if (landDTO == null) {
                                    player.sendMessage("Could not find your land")
                                    return@executesPlayer
                                } else if (landDTO.memberIDs != null && landDTO.memberIDs.contains(memberUuid.toString())) {
                                    player.sendMessage("Player is already a member")
                                    return@executesPlayer
                                }
                                val validateCommand = ValidateCommand.Builder(player)
                                    .checkPrimaryGroup()
                                    .checkRankConfiguration()
                                    .build()
                                if (!validateCommand.passed()) {
                                    player.sendMessage("We could not pass the validation of the command.")
                                    return@executesPlayer
                                }
                                val builder = validateCommand.builder
                                val rankConfiguration = builder.rankConfiguration
                                val memberIDs = landDTO.memberIDs
                                if (memberIDs != null && memberIDs.size >= rankConfiguration.memberSlots) {
                                    player.sendMessage("You've reached your limit for setting new land homes.")
                                    return@executesPlayer
                                }
                                if (!ConfirmCommandManager.isConfirmed(player)) {
                                    ConfirmCommandManager.requestCommandConfirmation(
                                        player,
                                        PayableCommand(
                                            "/land member add " + newMember.name,
                                            java.util.ArrayList(),
                                            main.pricesConfig.landMemberAddPrice,
                                            30L,
                                            rankConfiguration.discountPercentage
                                        )
                                    )
                                    return@executesPlayer
                                }
                                updateLandDTO.connectMembers.add(MemberDTO(memberUuid.toString()))
                                val result: LandDTO = api.landAPI.fetch.update(landDTO.id, updateLandDTO)
                                if (result == null) {
                                    player.sendMessage("Could not update your land member.")
                                    return@executesPlayer
                                }
                                player.sendMessage(newMember.name + " has been added as a member to your land")
                            })
                    ) // ### /land member remove <name> ###
                    .withSubcommand(
                        CommandAPICommand("remove")
                            .withArguments(PlayerArgument("name"))
                            .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any> ->
                                val oldMember = args[0] as OfflinePlayer
                                val uuid = player.uniqueId
                                val memberUuid = oldMember.uniqueId
                                val updateLandDTO = UpdateLandDTO()
                                val landDTO: LandDTO = api.landAPI.redis.get(uuid.toString())
                                if (landDTO == null) {
                                    player.sendMessage("Could not find your land")
                                    return@executesPlayer
                                } else if (landDTO.memberIDs != null && !landDTO.memberIDs.contains(memberUuid.toString())) {
                                    player.sendMessage("Player is not a member")
                                    return@executesPlayer
                                }
                                updateLandDTO.disconnectMembers.add(MemberDTO(memberUuid.toString()))
                                val result: LandDTO = api.landAPI.fetch.update(landDTO.id, updateLandDTO)
                                if (result == null) {
                                    player.sendMessage("Could not update your land member.")
                                    return@executesPlayer
                                }
                                player.sendMessage(oldMember.name + " has been removed from your land")
                            })
                    )
            ) // ### /land generate ###
            .withSubcommand(CommandAPICommand("generate")
                .withRequirement { obj: CommandSender -> obj.isOp }
                .withPermission(CommandPermission.OP)
                .withArguments(BooleanArgument("isClaimable"))
                .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any> ->
                    val isClaimable = args[0] as Boolean
                    val chunk = player.chunk

                    // Only continue if we're in the overworld
                    if (chunk.world != main.serverConfig.overworld) {
                        player.sendMessage("You can only generate chunks in the overworld.")
                        return@executesPlayer
                    }
                    val chunkDTO: ChunkDTO = api.chunkAPI.create(chunk, isClaimable)
                    if (chunkDTO == null) {
                        player.sendMessage("Error when trying to create a chunk")
                        return@executesPlayer
                    }
                    player.sendMessage("Chunk has been generated")
                })
            ) // ### /land extend ###
            .withSubcommand(
                CommandAPICommand("extend")
                    .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?>? ->
                        val chunk = player.chunk
                        val playerUuid = player.uniqueId
                        val currentWorld = chunk.world

                        // Only continue if we're in the OverWorld
                        if (currentWorld != overworld) {
                            player.sendMessage("You can only extend chunks in the overworld.")
                            return@executesPlayer
                        }

                        // Check if the player is within the blacklisted chunk radius
                        if (serverConfig.isWithinBlacklistedChunkRadius(player.location)) {
                            player.sendMessage("The chunk you're currently standing on is considered 'blacklisted' and not claimable.")
                            return@executesPlayer
                        }
                        val chunkDTO: ChunkDTO = main.api.chunkAPI.get(chunk)

                        // Verify if the chunk is null or not claimable.
                        // Players should be required to claim the land
                        // they're standing on
                        if (chunkDTO == null || !ChunkAPI.isClaimable(chunkDTO)) {
                            player.sendMessage("The chunk you're standing on is either non-claimable or not found. Maybe try to claim it first?")
                            return@executesPlayer
                        }
                        val landId = chunkDTO.landId

                        // Verify if the chunk can expire. This is
                        // required because we're checking for 'expirable'
                        // chunks only.
                        if (!chunkDTO.canExpire()!!) {
                            player.sendMessage(PluginTextComponent.warning("This thunk is permanent and cannot be extended."))
                            return@executesPlayer
                        }
                        val landDTO: LandDTO = main.api.landAPI.get(landId!!)

                        // Verify again if we actually have a land with the chunkId.
                        // This will prevent players from accessing the command and
                        // claiming land that is not theirs.
                        if (landDTO == null) {
                            player.sendMessage("You cannot extend an unclaimed chunk.")
                            return@executesPlayer
                        }

                        // Verify if the chunk is actually owner by the player. You
                        // can only extend your own chunks.
                        if (UUID.fromString(landDTO.ownerId) != playerUuid) {
                            player.sendMessage("This land is not yours.")
                            return@executesPlayer
                        }

                        // Go through the validation process of the player.
                        val validateCommand = ValidateCommand.Builder(player)
                            .checkPrimaryGroup()
                            .checkRankConfiguration()
                            .build()
                        if (!validateCommand.passed()) {
                            player.sendMessage("We could not pass the validation of the command.")
                            return@executesPlayer
                        }
                        val builder = validateCommand.builder
                        val rankConfiguration = builder.rankConfiguration

                        // Make a new payable command.
                        if (!ConfirmCommandManager.isConfirmed(player)) {
                            ConfirmCommandManager.requestCommandConfirmation(
                                player,
                                PayableCommand(
                                    "/land extend",
                                    java.util.ArrayList(),
                                    2000.0,  // TODO: PricesConfig
                                    30L,
                                    rankConfiguration.discountPercentage
                                )
                            )
                            return@executesPlayer
                        }
                        val dayCount = 7
                        val isExtended: Boolean = main.api.chunkAPI.extend(chunk, dayCount)
                        if (!isExtended) {
                            player.sendMessage("Oops, Could not extend your land.")
                            return@executesPlayer
                        }
                        player.sendMessage("We've extended your chunk with another $dayCount days. Do keep in mind that only counts for the chunk you're standing on.")
                    })
            )
            .withSubcommand(CommandAPICommand("debug")
                .withRequirement { obj: CommandSender -> obj.isOp }
                .withPermission(CommandPermission.OP)
                .withSubcommand(
                    CommandAPICommand("blacklistradius")
                        .executesPlayer(PlayerCommandExecutor { player: Player, args: Array<Any?>? ->
                            val playerLocation = player.location
                            val center = Location(player.world, 0.0, playerLocation.y, 0.0)
                            val blockRadius = main.serverConfig.blacklistedBlockRadius
                            val chunkRadius = main.serverConfig.blacklistedChunkRadius
                            val isInRadiusConfig = main.serverConfig.isWithinBlacklistedChunkRadius(playerLocation)
                            val isInRadiusUtil = LocationUtil.isWithinRadius(center, playerLocation, blockRadius)
                            player.sendMessage("isInRadiusConfig: $isInRadiusConfig")
                            player.sendMessage("isInRadiusUtil: $isInRadiusUtil")
                            player.sendMessage(
                                String.format(
                                    "playerLocation: X=%.2f, Y=%.2f, Z=%.2f",
                                    playerLocation.x,
                                    playerLocation.y,
                                    playerLocation.z
                                )
                            )
                            player.sendMessage(
                                String.format(
                                    "center: X=%.2f, Y=%.2f, Z=%.2f",
                                    center.x,
                                    center.y,
                                    center.z
                                )
                            )
                            player.sendMessage("blockRadius: $blockRadius")
                            player.sendMessage("chunkRadius: $chunkRadius")
                        })
                )
            )
            .register()
    }
}
