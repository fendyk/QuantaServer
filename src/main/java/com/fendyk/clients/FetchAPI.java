package com.fendyk.clients;

import com.fendyk.Log;
import com.fendyk.Main;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public abstract class FetchAPI<K, DTO, UpdateDTO> {
    protected final boolean inDebugMode;
    protected Main server;
    protected OkHttpClient client =  new OkHttpClient();
    protected final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    protected final String url;

    public FetchAPI(Main server, String url, boolean inDebugMode) {
        this.server = server;
        this.url = url;
        this.inDebugMode = inDebugMode;
    }
    void logDebug(Response res, String responseBody, String requestBody, String name) {
        Bukkit.getLogger().info("-----------------------------------");
        Bukkit.getLogger().info("Name: " + name);
        Bukkit.getLogger().info("ResponseCode: " + res.code());
        Bukkit.getLogger().info("Message: " + res.message());
        Bukkit.getLogger().info("Request:" + requestBody);
        Bukkit.getLogger().info("Response:" + responseBody);
        Bukkit.getLogger().info("-----------------------------------");
    }


    /**
     * Fetches from api
     * @param request
     * @param name
     * @return
     */
    @Nullable
    protected JsonElement fetchFromApi(Request request, String name) {
        try (Response response = client.newCall(request).execute()) {
            if(response.code() == 204) {
                Bukkit.getLogger().info(Log.Info("Response empty (204) at:" + name));
                if(inDebugMode) logDebug(response,
                        JsonNull.INSTANCE.toString(),
                        request.body() != null ? request.body().toString() : null,
                        name
                );
                return null; // Return empty
            }
            else if(response.code() != 200) {
                Bukkit.getLogger().info(Log.Error("Response not ok (" + response.code() + ") at:" + name));
                if(inDebugMode) logDebug(response,
                        response.body() != null ? response.body().string() : null,
                        request.body() != null ? request.body().toString() : null,
                        name
                );
                return null;
            }

            /* If no 'body' found we simpy return a JsonNull */
            if(response.body() == null) {
                return null; // Return empty
            }

            String json = response.body().string();
            if(inDebugMode) logDebug(response, json, request.body() != null ? request.body().toString() : null, name);
            return JsonParser.parseString(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public abstract DTO get(K key);

    @Nullable
    public abstract DTO create(DTO data);

    @Nullable
    public abstract DTO update(K key, UpdateDTO data);

    @Nullable
    public abstract DTO delete(K key);
}
