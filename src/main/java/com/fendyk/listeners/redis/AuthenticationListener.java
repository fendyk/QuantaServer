package com.fendyk.listeners.redis;

import com.fendyk.Main;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AuthenticationListener implements RedisPubSubListener<String, String> {

    Main main = Main.getInstance();

    @Override
    public void message(String channel, String message) {
        if (!channel.equals("authentication")) return;
        Bukkit.getLogger().info("Authentication");
        Bukkit.getLogger().info(channel + ", " + message);

        // TODO: We're listening for data, but what happens after?
        JsonObject data = JsonParser.parseString(message).getAsJsonObject();
        UUID uuid = UUID.fromString(data.get("uuid").getAsString());
        String eventName = data.get("event").getAsString();
        Player player = Bukkit.getPlayer(uuid);
        World world = Bukkit.getWorld(main.getServerConfig().getWorldName());
    }

    @Override
    public void message(String pattern, String channel, String message) {
    }

    @Override
    public void subscribed(String channel, long count) {

    }

    @Override
    public void psubscribed(String pattern, long count) {

    }

    @Override
    public void unsubscribed(String channel, long count) {

    }

    @Override
    public void punsubscribed(String pattern, long count) {

    }
}
