package com.fendyk.managers;

import com.fendyk.Main;
import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.UUID;

public class ChunkManager {

    Main main = Main.getInstance();

    static HashMap<UUID, Chunk> currentPlayerLocationChunks = new HashMap<>();

    public static HashMap<UUID, Chunk> getCurrentPlayerLocationChunks() {
        return currentPlayerLocationChunks;
    }
}
