package com.fendyk.listeners;

import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.Main;
import com.fendyk.managers.WorldguardSyncManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Objects;
import java.util.logging.Level;

public class ChunkListener implements RedisPubSubListener<String, String> {

    Main server;

    public ChunkListener(Main server) {
        this.server = server;
    }

    @Override
    public void message(String channel, String message) {
        if (!channel.equals("chunk")) {
            return;
        }

        Bukkit.getLogger().info("Chunk");
        Bukkit.getLogger().info(channel + ", " + message);

        World world = Bukkit.getWorld(server.serverConfig.getWorldName());
        JsonObject data = JsonParser.parseString(message).getAsJsonObject();
        String eventName = data.get("event").getAsString();
        JsonObject chunkObject = data.getAsJsonObject("chunk");
        ChunkDTO chunkDTO = Main.gson.fromJson(chunkObject, ChunkDTO.class);

        if (chunkDTO != null && world != null) {
            Chunk chunk = world.getChunkAt(chunkDTO.x, chunkDTO.z);
            Bukkit.getScheduler().runTask(server, () -> {
                try {
                    WorldguardSyncManager.syncChunkWithRegion(chunk, chunkDTO, null);
                } catch (StorageException e) {
                    throw new RuntimeException(e);
                }
            });
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
