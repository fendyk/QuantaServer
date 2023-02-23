package com.fendyk;

import com.fendyk.commands.EconomyCommands;
import com.fendyk.commands.LandCommands;
import com.fendyk.configs.EarningsConfig;
import com.fendyk.listeners.redis.AuthenticationListener;
import com.fendyk.listeners.redis.UserListener;
import com.fendyk.listeners.redis.minecraft.EntityDeathListener;
import com.google.gson.*;
import de.leonhard.storage.Toml;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class Main extends JavaPlugin implements Listener {

    API api;
    public static Gson gson = new Gson();
    EarningsConfig earningsConfig;
    Toml toml;

    public EarningsConfig getEarningsConfig() {return earningsConfig;}
    public API getApi() {return api;}

    @Override
    public void onEnable() {
        toml = new Toml("config", "plugins/Main");
        toml.setDefault("isInDebugMode", false);
        toml.setDefault("apiUrl", "<your apiUrl here>");
        toml.setDefault("redisUrl", "redis://password@localhost:6379/0");

        // Configs
        earningsConfig = new EarningsConfig();

        ArrayList<RedisPubSubListener<String, String>> listeners = new ArrayList<>();
        listeners.add(new AuthenticationListener());
        listeners.add(new UserListener());

        HashMap<String, String> subscriptions = new HashMap<>();
        subscriptions.put("authentication", "user");

        // Instantiate api
        api = new API(this, toml, listeners, subscriptions);

        // Commands
        new EconomyCommands(api);
        new LandCommands(api);

        // Listeners
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);

    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
    }

}