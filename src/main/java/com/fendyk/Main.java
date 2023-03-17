package com.fendyk;

import com.fendyk.commands.*;
import com.fendyk.configs.EarningsConfig;
import com.fendyk.configs.ServerConfig;
import com.fendyk.listeners.minecraft.*;
import com.fendyk.managers.ActivityBossBarManager;
import com.fendyk.managers.ActivityEarningsManager;
import com.fendyk.managers.ConfirmCommandManager;
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
    ServerConfig serverConfig;

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

        WorldguardSyncManager.server = this;
        ActivityBossBarManager.watch(); // Watch for changes
        ConfirmCommandManager.watch(); // Watch for changes

        // Configs
        serverConfig = new ServerConfig(this);
        earningsConfig = new EarningsConfig();
        ActivityEarningsManager.earningsConfig = earningsConfig;

        // Instantiate api
        api = new API(this);

        // Commands
        new ConfirmCommands();
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
        org.bukkit.World world = Bukkit.getWorld(serverConfig.getWorldName());
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
        String worldName = serverConfig.getWorldName();
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

    public ServerConfig getServerConfig() {return serverConfig;}
}