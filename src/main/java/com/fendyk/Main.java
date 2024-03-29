package com.fendyk;

import com.fendyk.commands.*;
import com.fendyk.configs.*;
import com.fendyk.expansions.QuantaExpansion;
import com.fendyk.listeners.minecraft.*;
import com.fendyk.managers.ActivityBossBarManager;
import com.fendyk.managers.ChunkManager;
import com.fendyk.managers.ConfirmCommandManager;
import com.fendyk.managers.WorldguardSyncManager;
import com.google.gson.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class Main extends JavaPlugin implements Listener {
    static Main instance;
    API api;
    public static Gson gson = new Gson();

    MessagesConfig messagesConfig;
    EarningsConfig earningsConfig;
    ServerConfig serverConfig;
    PricesConfig pricesConfig;
    RanksConfig ranksConfig;

    public EarningsConfig getEarningsConfig() {return earningsConfig;}
    public API getApi() {return api;}

    List<UUID> frozenPlayers;

    RegionManager overworldRegionManager;
    RegionManager endRegionManager;
    RegionManager netherRegionManager;
    FlagRegistry registry;
    LuckPerms luckPermsApi;
    BukkitAudiences adventure;
    public Main() {
        instance = this;
    }

    public static final StateFlag BARBARIAN_BUILD = new StateFlag("barbarian-build", true);

    @Override
    public void onEnable() {
        frozenPlayers = new ArrayList<>();
        this.adventure = BukkitAudiences.create(this);

        // Watch for changes
        ActivityBossBarManager.watch();
        ConfirmCommandManager.watch();
        ChunkManager.watch();

        // Configs
        messagesConfig = new MessagesConfig();
        serverConfig = new ServerConfig();
        earningsConfig = new EarningsConfig();
        pricesConfig = new PricesConfig();
        ranksConfig= new RanksConfig();

        // Instantiate api
        api = new API();

        // Commands
        new GeneralCommands();
        new PreferencesCommands();
        new TeleportationCommands();
        new EconomyCommands();
        new ConfirmCommands();
        new QuantaCommands();
        new LandCommands(this);
        new ActivityCommands(api);
        new RewardCommands(this);

        // Setup Plugin libraries
        setupWorldGuard();
        luckPermsApi = LuckPermsProvider.get();

        // Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkLoadListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerTeleportListener(), this);

        // PlaceholderAPI expansion
        new QuantaExpansion().register();

        // Initialize spawn region
        org.bukkit.World world = Bukkit.getWorld(serverConfig.getWorldName());
        try {
            WorldguardSyncManager.initialize(
                    getServerConfig().getBlacklistedChunkRadius(),
                    world != null ? world.getMinHeight() : -64,
                    world != null ? world.getMaxHeight() : 319
            );
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onLoad() {
        registry = WorldGuard.getInstance().getFlagRegistry();
        registry.register(BARBARIAN_BUILD);
    }

    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }


    public @NonNull BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    private void setupWorldGuard() {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        String worldName = serverConfig.getWorldName();
        World world = BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld(worldName)));
        World nether = BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld("world_nether")));
        World end = BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld("world_the_end")));
        this.overworldRegionManager = container.get(world);
        this.netherRegionManager = container.get(nether);
        this.endRegionManager = container.get(end);
    }

    public FlagRegistry getFlagRegistry() {
        return registry;
    }
    public static Main getInstance() {
        return instance;
    }
    public LuckPerms getLuckPermsApi() {
        return luckPermsApi;
    }
    public List<UUID> getFrozenPlayers() {
        return frozenPlayers;
    }
    public RegionManager getOverworldRegionManager() {
        return overworldRegionManager;
    }

    public RegionManager getEndRegionManager() {
        return endRegionManager;
    }

    public RegionManager getNetherRegionManager() {
        return netherRegionManager;
    }

    public ServerConfig getServerConfig() {return serverConfig;}
    public PricesConfig getPricesConfig() {return pricesConfig;}
    public RanksConfig getRanksConfig() {return ranksConfig;}
    public MessagesConfig getMessagesConfig() {return messagesConfig;}
}