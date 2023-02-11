package com.fendyk.clients;

import com.fendyk.Log;
import com.fendyk.QuantaServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

public abstract class FetchAPI<T> {

    protected final boolean inDebugMode;
    protected QuantaServer server;
    protected OkHttpClient client =  new OkHttpClient();
    protected final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    protected final String url;

    public FetchAPI(QuantaServer server, String url, boolean inDebugMode) {
        this.server = server;
        this.url = url;
        this.inDebugMode = inDebugMode;
    }

    /**
     * Fetches from api
     * @param request
     * @param name
     * @return
     */
    @Nullable
    protected JsonObject fetchFromApi(Request request, String name) {
        try (Response response = client.newCall(request).execute()) {
            if(response.code() == 204) {
                Bukkit.getLogger().info(Log.Info("Response empty (204) at:" + name));
                return new JsonObject(); // Return empty
            }
            else if(response.code() != 200) {
                Bukkit.getLogger().info(Log.Error("Response not ok (" + response.code() + ") at:" + name));
                return null;
            }

            assert response.body() != null;
            String json = response.body().string();
            if(inDebugMode) Bukkit.getLogger().info(json);
            return JsonParser.parseString("").getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract JsonObject get(T key);
    public abstract JsonObject create(JsonObject data);
    public abstract JsonObject update(T key, JsonObject data);
    public abstract JsonObject delete(T key);


    @Override
    public JsonObject getChunk(Chunk chunk) {
        final int x = chunk.getX();
        final int z = chunk.getZ();
        Request request = new Request.Builder()
                .url(url + "/chunks?x=" + x + "&z=" + z)
                .get()
                .build();
        return fetchFromApi(request, "getChunk");
    }

    @Override
    public JsonObject createChunk(Chunk chunk) {
        //TODO: add body
        final int x = chunk.getX();
        final int z = chunk.getZ();
        Request request = new Request.Builder()
                .url(url + "/chunks")
                .post()
                .build();
        return fetchFromApi(request, "createChunk");
    }

    @Override
    public JsonObject claimChunk(Chunk chunk, UUID player) {
        final int x = chunk.getX();
        final int z = chunk.getZ();
        Request request = new Request.Builder()
                .url(url + "/chunks/")
                .post()
                .build();
        return fetchFromApi(request, "createChunk");
    }

    @Override
    public boolean createLand(UUID owner, String name, Chunk chunk) {
        return false;
    }

    @Override
    public boolean claimChunkForLand(UUID owner, Chunk chunk) {
        return true;
    }

    @Override
    public JsonObject getLand(UUID owner) {
        return null;
    }
}
