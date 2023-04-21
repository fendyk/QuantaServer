package com.fendyk.clients;

import com.fendyk.utilities.Log;
import com.fendyk.Main;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public abstract class FetchAPI<K, DTO, UpdateDTO> {
    protected final boolean inDebugMode;
    protected Main server;
    protected OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build();
    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected final String url;
    protected final String authHeader = "Authorization";
    protected Request.Builder requestBuilder;

    public FetchAPI(Main server, String url, boolean inDebugMode, String authKey) {
        this.server = server;
        this.url = url;
        this.inDebugMode = inDebugMode;
        this.requestBuilder = new Request.Builder()
                .addHeader(authHeader, "Bearer " + authKey);
    }

    /**
     * Fetches from api
     *
     * @param request
     * @param name
     * @return
     */
    @Nullable
    protected JsonElement fetchFromApi(Request request, String name) {
        try (Response response = client.newCall(request).execute()) {
            if (inDebugMode) {
                Log.info("");
                Log.info("FETCH: fetchFromApi is called at: " + name);
                Log.info("Request URL: " + request.url());
                Log.info("Response Code: " + response.code());
                Log.info("Response Message: " + response.message());
                Log.info("");
            }

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }

            try (ResponseBody body = response.body()) {
                if (body == null) return null;
                String json = body.string();
                if (inDebugMode) {
                    Log.info("Response Body: " + json);
                }
                return JsonParser.parseString(json);
            }
        } catch (IOException e) {
            Log.error("Error in fetchFromApi: " + e.getMessage());
            Log.error("Stacktrace: " + Arrays.toString(e.getStackTrace()));
            return null;
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
