package com.fendyk;

import com.fendyk.commands.EconomyCommands;
import com.fendyk.commands.LandCommands;
import com.fendyk.configs.EarningsConfig;
import com.fendyk.listeners.redis.*;
import com.fendyk.listeners.redis.minecraft.ChunkLoadListener;
import com.fendyk.listeners.redis.minecraft.EntityDeathListener;
import com.google.gson.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.leonhard.storage.Toml;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Main extends JavaPlugin implements Listener {

    API api;
    public static Gson gson = new Gson();
    EarningsConfig earningsConfig;
    Toml config;

    public EarningsConfig getEarningsConfig() {return earningsConfig;}
    public API getApi() {return api;}

    RegionManager regionManager;

    @Override
    public void onEnable() {
        config = new Toml("config", "plugins/QuantaServer");
        config.setDefault("isInDebugMode", false);
        config.setDefault("apiUrl", "<your apiUrl here>");
        config.setDefault("redisUrl", "redis://password@localhost:6379/0");
        config.setDefault("worldName", "overworld");

        // Configs
        earningsConfig = new EarningsConfig();
        // Instantiate api
        api = new API(this, config);

        // Commands
        new EconomyCommands(api);
        new LandCommands(api);

        // Setup WorldGuard
        setupWorldguard();

        // Listeners
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new ChunkLoadListener(this), this);

    }

    private void setupWorldguard() {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        String worldName = config.getString("worldName");
        World world = BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld(worldName)));
        this.regionManager = container.get(world);
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