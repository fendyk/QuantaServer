package com.fendyk.clients;

import com.fendyk.QuantaServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Chunk;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class RedisAPI implements ClientAPI {

    QuantaServer server;

    public RedisAPI(QuantaServer server) {
        this.server = server;
    }

    @Override
    public JsonObject getMinecraftUser(UUID player) {
        return JsonParser.parseString(
                server.getRedisSyncCommands().get("minecraftuser:" + player.toString())
        ).getAsJsonObject();
    }

    @Override
    public boolean setMinecraftUser(UUID player, JsonObject data) {
        JsonObject json = JsonParser.parseString(
                server.getRedisSyncCommands().get("minecraftuser:" + player.toString())
        ).getAsJsonObject();

        return json == null;
    }

    @Override
    public BigDecimal getPlayerBalance(UUID player) {
        JsonObject jPlayer = getMinecraftUser(player);
        return jPlayer.get("quanta").getAsBigDecimal();
    }

    @Override
    public boolean depositBalance(UUID player, BigDecimal amount) {
        JsonObject jPlayer = getMinecraftUser(player);

        BigDecimal oldAmount = jPlayer.get("quanta").getAsBigDecimal();
        BigDecimal newAmount = oldAmount.add(amount).setScale(2, RoundingMode.HALF_EVEN);
        jPlayer.addProperty("quanta", newAmount);

        return setMinecraftUser(player, jPlayer);
    }

    @Override
    public boolean withdrawBalance(UUID player, BigDecimal amount) {
        JsonObject jPlayer = getMinecraftUser(player);
        BigDecimal oldAmount = jPlayer.get("quanta").getAsBigDecimal();
        BigDecimal newAmount = oldAmount.subtract(amount).setScale(2, RoundingMode.HALF_EVEN);

        if(oldAmount.compareTo(amount) < 0) {
            return false;
        }

        jPlayer.addProperty("quanta", newAmount);

        return setMinecraftUser(player, jPlayer);
    }

    @Override
    public JsonObject getChunk(Chunk chunk) {
        final int x = chunk.getX();
        final int y = chunk.getZ();

        return JsonParser.parseString(
                server.getRedisSyncCommands().get("chunk:" + x + ":" + y)
        ).getAsJsonObject();
    }

    @Override
    public void createLand(UUID owner, String name, Chunk chunk) {

    }

    @Override
    public void claimChunkForLand(UUID owner, Chunk chunk) {

    }

    @Override
    public JsonObject getLand(UUID owner) {
        return JsonParser.parseString(
                server.getRedisSyncCommands().get("land:" + owner.toString())
        ).getAsJsonObject();
    }

}
