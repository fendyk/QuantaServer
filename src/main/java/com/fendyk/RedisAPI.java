package com.fendyk;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.util.UUID;

public class RedisAPI {
    QuantaServer server;

    public RedisAPI(QuantaServer server) {
        this.server = server;
    }

    /**
     * Deposits x amount to the player
     * @param amount
     */
    public void depositBalance(UUID player, BigDecimal amount) {
        JsonObject jPlayer = getMinecraftUser(player);

        BigDecimal oldAmount = jPlayer.get("quanta").getAsBigDecimal();
        BigDecimal newAmount = oldAmount.add(amount);
        jPlayer.addProperty("quanta", newAmount);

        Bukkit.getLogger().info(amount.toString());
        Bukkit.getLogger().info(oldAmount.toString());
        Bukkit.getLogger().info(jPlayer.toString());

        setMinecraftUser(player, jPlayer);

        // Fire event that specific player needs to be updated into the database

    }

    /**
     * Deposits x amount to the player
     * @param player
     */
    public BigDecimal getPlayerBalance(UUID player) {
        JsonObject jPlayer = getMinecraftUser(player);
        return jPlayer.get("quanta").getAsBigDecimal();
    }

    /**
     * Gets the minecraft user data
     * @param player
     * @return
     */
    public JsonObject getMinecraftUser(UUID player) {
        return JsonParser.parseString(
                getMinecraftUserJsonString(player)
        ).getAsJsonObject();
    }

    /**
     * Gets the minecraft user data by string
     * @param player
     * @return
     */
    public String getMinecraftUserJsonString(UUID player) {
        return server.getRedisSyncCommands().get("minecraftuser:" + player.toString());
    }

    /**
     * Sets the minecraft user
     * @param player
     * @param data
     */
    protected void setMinecraftUser(UUID player, JsonObject data) {
        server.getRedisSyncCommands().set("minecraftuser:" + player.toString(), data.toString());
    }

    /**
     * Deposits x amount to the player
     * @param amount
     */
    public void withdrawBalance(UUID player, BigDecimal amount) {
        JsonObject jPlayer = getMinecraftUser(player);
        BigDecimal oldAmount = jPlayer.get("quanta").getAsBigDecimal();
        BigDecimal newAmount = oldAmount.subtract(amount);

        if(newAmount.compareTo(BigDecimal.ZERO) < 0) {
            return;
        }

        oldAmount.add(amount);
        jPlayer.addProperty("quanta", oldAmount);

        setMinecraftUser(player, jPlayer);
    }

}
