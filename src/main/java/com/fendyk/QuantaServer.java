package com.fendyk;

import com.fendyk.commands.EconomyCommands;
import com.fendyk.listeners.redis.AuthenticationListener;
import com.fendyk.listeners.redis.UserListener;
import com.google.gson.*;
import de.leonhard.storage.Toml;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class QuantaServer extends JavaPlugin implements Listener {

    Api api;
    RedisAPI redisAPI;
    private Gson gson = new Gson();
    Toml toml;
    RedisClient redisClient;
    StatefulRedisConnection<String, String> redisConnection;
    StatefulRedisPubSubConnection<String, String> redisPubSubConnection;
    RedisCommands<String, String> redisSyncCommands;
    RedisPubSubCommands<String, String> redisPubSubSyncCommands;

    public RedisCommands<String, String> getRedisSyncCommands() {
        return redisSyncCommands;
    }

    @Override
    public void onEnable() {
        toml = new Toml("config", "plugins/QuantaServer");
        toml.setDefault("isInDebugMode", false);
        String apiUrl = toml.getOrSetDefault("apiUrl", "<your apiUrl here");
        String redisUrl = toml.getOrSetDefault("redisUrl", "redis://password@localhost:6379/0");

        redisAPI = new RedisAPI(this);
        api = new Api(apiUrl, toml);

        // Commands
        new EconomyCommands(redisAPI);

        // Listeners
        getServer().getPluginManager().registerEvents(this, this);

        redisClient = RedisClient.create(redisUrl);
        redisConnection = redisClient.connect();
        redisSyncCommands = redisConnection.sync();
        redisPubSubConnection = redisClient.connectPubSub();

        // Add the listener
        redisPubSubConnection.addListener(new AuthenticationListener());
        redisPubSubConnection.addListener(new UserListener());

        // Always sync after adding listeners
        redisPubSubSyncCommands = redisPubSubConnection.sync();

        // Subscribe
        redisPubSubSyncCommands.subscribe("authentication", "user");

        if(!redisConnection.isOpen()) {
            Bukkit.getLogger().info(Log.Error("Redis connection is not open!"));
        }
        else {
            Bukkit.getLogger().info(Log.Success("Redis connection success!"));
        }


    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
    }

}