package com.fendyk;

import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.FetchAPI;
import com.fendyk.clients.RedisAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.leonhard.storage.Toml;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class API implements ClientAPI {

    OkHttpClient client =  new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    final String url;
    final boolean isInDebugMode;
    QuantaServer server;

    private RedisAPI redisAPI;
    private FetchAPI fetchAPI;

    public RedisAPI getRedisAPI() {return this.redisAPI;}
    public FetchAPI getFetchAPI() {return this.fetchAPI;}

    public API(QuantaServer server, Toml config) {
        this.server = server;
        this.url = config.getString("apiUrl");
        this.isInDebugMode = config.getBoolean("isInDebugMode");

        this.redisAPI = new RedisAPI(server);
    }

    @Override
    public JsonObject getMinecraftUser(UUID player) {
        JsonObject json = redisAPI.getMinecraftUser(player);
        return json != null ? json : fetchAPI.getMinecraftUser(player);
    }

    @Override
    public boolean setMinecraftUser(UUID player, JsonObject data) {
        boolean isSet = redisAPI.setMinecraftUser(player, data);
        return isSet || fetchAPI.setMinecraftUser(player, data);
    }

    @Override
    public BigDecimal getPlayerBalance(UUID player) {
        BigDecimal amount = redisAPI.getPlayerBalance(player);
        return amount != null ? amount : fetchAPI.getPlayerBalance(player);
    }

    @Override
    public boolean depositBalance(UUID player, BigDecimal amount) {
        boolean isSet = redisAPI.depositBalance(player, amount);
        return isSet || fetchAPI.depositBalance(player, amount);
    }

    @Override
    public boolean withdrawBalance(UUID player, BigDecimal amount) {
        boolean isSet = redisAPI.withdrawBalance(player, amount);
        return isSet || fetchAPI.withdrawBalance(player, amount);
    }

    @Override
    public JsonObject getChunk(Chunk chunk) {
        JsonObject json = redisAPI.getChunk(chunk);
        return json != null ? json : fetchAPI.getChunk(chunk);
    }

    @Override
    public void createLand(UUID owner, String name, Chunk chunk) {

    }

    @Override
    public void claimChunkForLand(UUID owner, Chunk chunk) {

    }

    @Override
    public JsonObject getLand(UUID owner) {
        return null;
    }
}
