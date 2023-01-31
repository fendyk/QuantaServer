package com.fendyk;

import com.fendyk.commands.EconomyCommands;
import com.fendyk.events.ProxyPlayerAuthenticatedEvent;
import com.fendyk.listeners.redis.AuthenticationListener;
import com.fendyk.listeners.redis.UserListener;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
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
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Map;
import java.util.UUID;

public class QuantaServer extends JavaPlugin implements PluginMessageListener, Listener {

    Api api;
    ChannelAPI channelAPI;
    private Gson gson = new Gson();
    private Map<UUID, String> users;
    Toml toml;
    RedisClient redisClient;
    StatefulRedisConnection<String, String> redisConnection;
    StatefulRedisPubSubConnection<String, String> redisPubSubConnection;
    RedisCommands<String, String> redisSyncCommands;
    RedisPubSubCommands<String, String> redisPubSubSyncCommands;

    @Override
    public void onEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "quanta:main");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "quanta:main", this);

        toml = new Toml("config", "plugins/QuantaServer");
        toml.setDefault("isInDebugMode", false);
        String apiUrl = toml.getOrSetDefault("apiUrl", "<your apiUrl here");
        String redisUrl = toml.getOrSetDefault("redisUrl", "redis://password@localhost:6379/0");

        channelAPI = new ChannelAPI(this);
        api = new Api(apiUrl, toml);

        // Commands
        new EconomyCommands(channelAPI);

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
        //make sure to unregister the registered channels in case of a reload
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();


    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("quanta:main")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String channelMessage = in.readUTF();
        System.out.println(channelMessage);

        JsonObject json = JsonParser.parseString(channelMessage).getAsJsonObject();

        if(!json.has("event")) {
            System.out.println("Event name not found, make sure it's set!");
            return;
        }
        else if(!json.has("data")) {
            System.out.println("Data not found. Make sure it's set!");
            return;
        }

        String eventName = json.getAsString();

        // Events are described here. Adding more requires
        // an update on the proxy as well.
        switch (eventName) {
            /**
             * Proxy -> Fired when player is authenticated and
             * has user data included
             */
            case "proxy:player:authenticated":
                Bukkit.getPluginManager().callEvent(new ProxyPlayerAuthenticatedEvent());
                break;
            /**
             * Proxy -> Fired when balance is requested or requires
             * an update
             */
            case "proxy:player:balance":
                // Fill in code when player is authenticated
                break;
            default:
                System.out.println("Event name not recognized");
                break;
        }

    }

}