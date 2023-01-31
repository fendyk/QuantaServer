package com.fendyk;

import com.fendyk.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import de.leonhard.storage.Toml;

import java.io.IOException;
import java.util.UUID;


public class Api {

    static OkHttpClient client =  new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    String url;
    Toml config;

    boolean isInDebugMode;

    public Api(String apiUrl, Toml config) {
        this.url = apiUrl;
        this.config = config;

        if(config != null) {
            isInDebugMode = config.getBoolean("isInDebugMode");
        }
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
            String json = response.body().string();
            if(isInDebugMode) Bukkit.getLogger().info(json);

            if(response.code() == 204) {
                Bukkit.getLogger().info(Log.Info("Response empty (204) at:" + name));
                return null;
            }
            else if(response.code() != 200) {
                Bukkit.getLogger().info(Log.Error("Response not ok (" + response.code() + ") at:" + name));
                return null;
            }
            return new JsonParser().parse(json).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetches the latest player data from the api
     * @param userId
     * @return
     */
    public JsonObject fetchUserData(String userId) {
        Request request = new Request.Builder()
                .url(url + "/users/" + userId)
                .get()
                .build();
        return fetchFromApi(request, "fetchUserData");
    }

    /**
     * Fetches minecraft user
     * @param uuid
     * @return
     */
    public JsonObject fetchMinecraftUser(UUID uuid) {
        Request request = new Request.Builder()
                .url(url + "/minecraftusers/" + uuid)
                .get()
                .build();
        return fetchFromApi(request, "fetchMinecraftUser");
    }

    /**
     * Fetches the latest subscription status from the server
     * @param userId
     * @return
     */
    public JsonObject fetchUserSubscriptionStatus(String userId) {
        Request request = new Request.Builder()
                .url(url + "/users/" + userId + "/get-subscription-status")
                .get()
                .build();
        return fetchFromApi(request, "fetchUserSubscriptionStatus");
    }

    /**
     * Fetches the balance of an player
     * @param player
     * @return
     */
    public JsonObject getBalance(UUID player) {
        Request request = new Request.Builder()
                .url(url + "/users/" + player.toString() + "/balance")
                .get()
                .build();
        return fetchFromApi(request, "getBalance");
    }


    public void fetchLand() {

    }

    public void fetchChunks() {

    }


}
