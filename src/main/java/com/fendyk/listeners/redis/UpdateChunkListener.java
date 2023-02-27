package com.fendyk.listeners.redis;

import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.Main;
import com.fendyk.managers.WorldguardSyncManager;
import com.google.gson.JsonParser;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.util.Objects;

public class UpdateChunkListener implements RedisPubSubListener<String, String> {

    Main server;

    public UpdateChunkListener(Main server) {
        this.server = server;
    }

    @Override
    public void message(String channel, String message)  {
        if(!channel.equals("chunkUpdateEvent")) return;

        Bukkit.getLogger().info("UpdateChunkListener");
        Bukkit.getLogger().info(channel + ", " + message);

        ChunkDTO chunkDTO = Main.gson.fromJson(message, ChunkDTO.class);
        if(chunkDTO == null) return;

        Chunk chunk = Objects.requireNonNull(
                Bukkit.getWorld(server.getTomlConfig().getString("worldName")))
                .getChunkAt(chunkDTO.getX(), chunkDTO.getZ()
        );

        try {
            WorldguardSyncManager.syncChunkWithRegion(chunk);
        } catch (StorageException e) {
            throw new RuntimeException(e);
        }


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