package com.fendyk;

import com.fendyk.commands.ActivityCommands;
import com.fendyk.commands.EconomyCommands;
import com.fendyk.commands.LandCommands;
import com.fendyk.configs.EarningsConfig;
import com.fendyk.listeners.minecraft.*;
import com.fendyk.managers.ActivityEarningsManager;
import com.fendyk.managers.WorldguardSyncManager;
import com.google.gson.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.leonhard.storage.Toml;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Main extends JavaPlugin implements Listener {

    API api;
    public static Gson gson = new Gson();
    EarningsConfig earningsConfig;
    Toml config;

    public EarningsConfig getEarningsConfig() {return earningsConfig;}
    public API getApi() {return api;}

    List<UUID> frozenPlayers;

    RegionManager regionManager;
    LuckPerms luckPermsApi;

    @Override
    public void onEnable() {
        frozenPlayers = new ArrayList<>();

        config = new Toml("config", "plugins/QuantaServer");
        config.setDefault("isInDebugMode", false);
        config.setDefault("apiUrl", "<your apiUrl here>");
        config.setDefault("redisUrl", "redis://password@localhost:6379/0");
        config.setDefault("worldName", "overworld");

        WorldguardSyncManager.server = this;

        // Configs
        earningsConfig = new EarningsConfig();
        ActivityEarningsManager.earningsConfig = earningsConfig;

        // Instantiate api
        api = new API(this, config);

        // Commands
        new EconomyCommands(api);
        new LandCommands(this);
        new ActivityCommands(api);

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

    }

    private void setupWorldGuard() {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        String worldName = config.getString("worldName");
        World world = BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld(worldName)));
        this.regionManager = container.get(world);
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

    public Toml getTomlConfig() {
        return this.config;
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
    }

}