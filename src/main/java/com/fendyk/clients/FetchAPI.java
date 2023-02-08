package com.fendyk.clients;

import com.fendyk.Log;
import com.fendyk.QuantaServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

public class FetchAPI implements ClientAPI {

    QuantaServer server;
    OkHttpClient client =  new OkHttpClient();

    public FetchAPI(QuantaServer server) {
        this.server = server;
    }

    /**
     * Fetches from api
     * @param request
     * @param name
     * @return
     */
    @Nullable
    private JsonObject fetchFromApi(Request request, String name) {
        try (Response response = client.newCall(request).execute()) {
            if(response.code() == 204) {
                Bukkit.getLogger().info(Log.Info("Response empty (204) at:" + name));
                return null;
            }
            else if(response.code() != 200) {
                Bukkit.getLogger().info(Log.Error("Response not ok (" + response.code() + ") at:" + name));
                return null;
            }

            assert response.body() != null;
            String json = response.body().string();
            if(isInDebugMode) Bukkit.getLogger().info(json);
            return JsonParser.parseString("").getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public JsonObject getMinecraftUser(UUID player) {
        return null;
    }

    @Override
    public boolean setMinecraftUser(UUID player, JsonObject data) {
        return false;
    }

    @Override
    public BigDecimal getPlayerBalance(UUID player) {
        return null;
    }

    @Override
    public boolean depositBalance(UUID player, BigDecimal amount) {
        return false;
    }

    @Override
    public boolean withdrawBalance(UUID player, BigDecimal amount) {
        return false;
    }

    @Override
    public JsonObject getChunk(Chunk chunk) {
        return null;
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
