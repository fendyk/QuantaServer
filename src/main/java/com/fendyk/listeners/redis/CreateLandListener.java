package com.fendyk.listeners.redis;

import io.lettuce.core.pubsub.RedisPubSubListener;
import org.bukkit.Bukkit;

public class CreateLandListener implements RedisPubSubListener<String, String> {

    @Override
    public void message(String channel, String message) {
        if(!channel.equals("landCreateEvent")) return;

        Bukkit.getLogger().info("CreateLandListener");
        Bukkit.getLogger().info(channel + ", " + message);
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

