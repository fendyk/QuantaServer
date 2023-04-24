package com.fendyk.listeners;

import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.LandDTO;
import com.fendyk.Main;
import com.fendyk.managers.WorldguardSyncManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

public class LandListener implements RedisPubSubListener<String, String> {

    Main server;

    public LandListener(Main server) {
        this.server = server;
    }


    @Override
    public void message(String channel, String message) {
        if (!channel.equals("land")) {
            return;
        }

        Bukkit.getLogger().info("Land");
        Bukkit.getLogger().info(channel + ", " + message);

        JsonObject data = JsonParser.parseString(message).getAsJsonObject();
        String eventName = data.get("event").getAsString();
        String worldName = server.serverConfig.getWorldName();
        JsonArray chunksObject = data.getAsJsonArray("chunks");
        JsonObject landObject = data.getAsJsonObject("land");

        LandDTO land = Main.gson.fromJson(landObject, LandDTO.class);
        ChunkDTO[] chunks = Main.gson.fromJson(chunksObject, ChunkDTO[].class);

        if (chunks == null || chunks.length < 1 || land == null) {
            return;
        }

        for (ChunkDTO chunkDTO : chunks) {
            Bukkit.getScheduler().runTask(server, () -> {
                Chunk chunk = Objects.requireNonNull(Bukkit.getWorld(worldName))
                        .getChunkAt(chunkDTO.x, chunkDTO.z);
                try {
                    WorldguardSyncManager.syncChunkWithRegion(chunk, chunkDTO, land);
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