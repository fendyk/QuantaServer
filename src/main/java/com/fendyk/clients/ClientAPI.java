package com.fendyk.clients;

import com.fendyk.QuantaServer;
import com.google.gson.JsonObject;
import org.bukkit.Chunk;

import java.math.BigDecimal;
import java.util.UUID;

public interface ClientAPI {
    public QuantaServer server = null;

    /**
     * Gets the minecraft user data
     * @param player
     * @return json object with data
     */
    public JsonObject getMinecraftUser(UUID player);

    /**
     * Sets the minecraft user data
     * @param player
     * @return true if success
     */
    public boolean setMinecraftUser(UUID player, JsonObject data);

    /**
     * Deposits x amount to the player
     * @param player
     * @return amount as BigDecimal
     */
    public BigDecimal getPlayerBalance(UUID player);

    /**
     * Deposits x amount to the player
     * @param player
     * @param amount
     * @return true if success
     */
    public boolean depositBalance(UUID player, BigDecimal amount);

    /**
     * Withdraws x amount to the player
     * @param player
     * @param amount
     * @return true if success
     */
    public boolean withdrawBalance(UUID player, BigDecimal amount);

    /**
     * Retrieves the chunk status
     * @param chunk
     */
    public JsonObject getChunk(Chunk chunk);

    /**
     * Creates a new land
     * @param owner
     * @param name
     * @param chunk
     */
    public void createLand(UUID owner, String name, Chunk chunk);

    /**
     * Claims a chunk for land
     * @param owner
     * @param chunk
     */
    public void claimChunkForLand(UUID owner, Chunk chunk);

    /**
     * Gets a land
     * @param owner
     */
    public JsonObject getLand(UUID owner);

}
