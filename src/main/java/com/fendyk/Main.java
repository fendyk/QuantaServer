package com.fendyk;

import com.fendyk.commands.ActivityCommands;
import com.fendyk.commands.EconomyCommands;
import com.fendyk.commands.LandCommands;
import com.fendyk.commands.RewardCommands;
import com.fendyk.configs.EarningsConfig;
import com.fendyk.listeners.minecraft.*;
import com.fendyk.managers.ActivityBossbarManager;
import com.fendyk.managers.ActivityEarningsManager;
import com.fendyk.managers.WorldguardSyncManager;
import com.google.gson.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.leonhard.storage.Toml;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class Main extends JavaPlugin implements Listener {

    static Main instance;
    API api;
    public static Gson gson = new Gson();
    EarningsConfig earningsConfig;
    Toml config;

    public EarningsConfig getEarningsConfig() {return earningsConfig;}
    public API getApi() {return api;}

    List<UUID> frozenPlayers;

    RegionManager regionManager;
    LuckPerms luckPermsApi;
    BukkitAudiences adventure;

    @Override
    public void onEnable() {
        instance = this;
        frozenPlayers = new ArrayList<>();
        this.adventure = BukkitAudiences.create(this);

        config = new Toml("config", "plugins/QuantaServer");
        config.setDefault("isInDebugMode", false);
        config.setDefault("apiUrl", "<your apiUrl here>");
        config.setDefault("redisUrl", "redis://password@localhost:6379/0");
        config.setDefault("worldName", "overworld");

        WorldguardSyncManager.server = this;
        ActivityBossbarManager.watch(); // Watch for changes

        // Configs
        earningsConfig = new EarningsConfig();
        ActivityEarningsManager.earningsConfig = earningsConfig;

        // Instantiate api
        api = new API(this, config);

        // Commands
        new EconomyCommands(api);
        new LandCommands(this);
        new ActivityCommands(api);
        new RewardCommands(this);

        // Setup Plugin libraries
        setupWorldGuard();
        luckPermsApi = LuckPermsProvider.get();

        // Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new ChunkLoadListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);

        // Initialize spawn region
        org.bukkit.World world = Bukkit.getWorld(config.getString("worldName"));
        try {
            WorldguardSyncManager.initializeSpawn(
                    Math.toIntExact(api.getBlacklistedChunkAPI().getRedis().hLen()),
                    world != null ? world.getMinHeight() : -64,
                    world != null ? world.getMaxHeight() : 319
            );
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }
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
        String worldName = config.getString("worldName");
        World world = BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld(worldName)));
        this.regionManager = container.get(world);
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

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public Toml getTomlConfig() {return this.config;}

}