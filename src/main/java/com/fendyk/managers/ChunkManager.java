package com.fendyk.managers;

import com.fendyk.Main;
import com.fendyk.utilities.Log;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.joda.time.DateTime;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkManager {

    static Main main = Main.getInstance();
    static ConcurrentHashMap<DateTime, Chunk> expirableChunks = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<DateTime, Chunk> getExpirableChunks() {
        return expirableChunks;
    }

    public static void watch() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
            expirableChunks.entrySet().removeIf(entry -> {
                DateTime expiredDate = entry.getKey();
                Chunk chunk = entry.getValue();
                // If expired
                if (expiredDate.isBeforeNow()) {
                    boolean isExpired = main.getApi().getChunkAPI().expire(chunk);
                    if(isExpired) Log.success("We've expired the chunk.");
                    return isExpired;
                }
                return false;
            });
        }, 0, 100L);
    }

}
