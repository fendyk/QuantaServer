package com.fendyk

import com.fendyk.commands.*
import com.fendyk.configs.*
import com.fendyk.expansions.QuantaExpansion
import com.fendyk.listeners.minecraft.*
import com.fendyk.managers.ActivityBossBarManager
import com.fendyk.managers.ChunkManager
import com.fendyk.managers.ConfirmCommandManager
import com.fendyk.managers.WorldguardSyncManager
import com.google.gson.Gson
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.StateFlag
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry
import com.sk89q.worldguard.protection.managers.RegionManager
import com.sk89q.worldguard.protection.managers.storage.StorageException
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class Main : JavaPlugin(), Listener {

    val api: API get() = API(this);
    val messagesConfig: MessagesConfig get() = MessagesConfig()
    val earningsConfig: EarningsConfig get() = EarningsConfig()
    val serverConfig: ServerConfig get() = ServerConfig()
    val pricesConfig: PricesConfig get() = PricesConfig()
    val ranksConfig: RanksConfig get() = RanksConfig()
    val frozenPlayers: List<UUID> get() = ArrayList()

    var overworldRegionManager: RegionManager? = null
    var endRegionManager: RegionManager? = null
    var netherRegionManager: RegionManager? = null
    var flagRegistry: FlagRegistry? = null

    var luckPermsApi: LuckPerms? = null
    var adventure: BukkitAudiences? = null

    init {
        instance = this
    }

    override fun onEnable() {
        adventure = BukkitAudiences.create(this)

        // Watch for changes
        ActivityBossBarManager.watch()
        ConfirmCommandManager.watch()
        ChunkManager.watch()

        // Commands
        GeneralCommands()
        PreferencesCommands()
        TeleportationCommands()
        EconomyCommands()
        ConfirmCommands()
        QuantaCommands()
        LandCommands()
        ActivityCommands()
        RewardCommands()

        // Setup Plugin libraries
        setupWorldGuard()
        luckPermsApi = LuckPermsProvider.get()

        // Listeners
        server.pluginManager.registerEvents(PlayerJoinListener(this), this)
        server.pluginManager.registerEvents(PlayerQuitListener(this), this)
        server.pluginManager.registerEvents(PlayerMoveListener(this), this)
        server.pluginManager.registerEvents(this, this)
        server.pluginManager.registerEvents(EntityDeathListener(this), this)
        server.pluginManager.registerEvents(ChunkLoadListener(this), this)
        server.pluginManager.registerEvents(BlockBreakListener(this), this)
        server.pluginManager.registerEvents(BlockPlaceListener(this), this)
        server.pluginManager.registerEvents(PlayerCommandListener(), this)
        //getServer().getPluginManager().registerEvents(new RedStoneListener(), this);

        // PlaceholderAPI expansion
        QuantaExpansion().register()

        // Initialize spawn region
        val world = Bukkit.getWorld(serverConfig.worldName)
        try {
            WorldguardSyncManager.initialize(
                    serverConfig.blacklistedChunkRadius,
                    world?.minHeight ?: -64,
                    world?.maxHeight ?: 319
            )
        } catch (e: StorageException) {
            throw RuntimeException(e)
        }
    }

    override fun onLoad() {
        val localFlagRegistry = WorldGuard.getInstance().flagRegistry
        flagRegistry = localFlagRegistry
        localFlagRegistry.register(BARBARIAN_BUILD)
    }

    override fun onDisable() {
        if (adventure != null) {
            adventure!!.close()
            adventure = null
        }
    }

    fun adventure(): BukkitAudiences {
        checkNotNull(adventure) { "Tried to access Adventure when the plugin was disabled!" }
        return adventure!!
    }

    private fun setupWorldGuard() {
        val container = WorldGuard.getInstance().platform.regionContainer
        val worldName = serverConfig.worldName
        val world = BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld(worldName)))
        val nether = BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld("world_nether")))
        val end = BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld("world_the_end")))
        overworldRegionManager = container[world]
        netherRegionManager = container[nether]
        endRegionManager = container[end]
    }

    companion object {
        @JvmStatic
        lateinit var instance: Main
        @JvmField
        var gson = Gson()
        @JvmField
        val BARBARIAN_BUILD = StateFlag("barbarian-build", true)
    }
}