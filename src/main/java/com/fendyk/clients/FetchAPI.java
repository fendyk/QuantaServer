package com.fendyk.clients;

import com.fendyk.DTOs.MinecraftUserDTO;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class FetchAPI<DTO, UpdateDTO> {
    static Main main = Main.getInstance();
    protected OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build();
    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected final static String authHeader = "Authorization";
    protected static Request.Builder requestBuilder;
    private static String url;

    private final Class<DTO> dtoType;
    private final String endpointUrl;

    public FetchAPI(String endpointUrl, Class<DTO> dtoType) {
        this.endpointUrl = endpointUrl;
        this.dtoType = dtoType;
    }

    /**
     * Fetches from api
     *
     * @param request
     * @param name
     * @return
     */
    @Nullable
    protected CompletableFuture<JsonElement> fetch(Request request, String name) {
        final boolean inDebugMode = main.getServerConfig().isInDebugMode();
        return CompletableFuture.supplyAsync(() -> {
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
                    if (body == null) throw new IOException("Could not find a body.");
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
        });
    }

    public CompletableFuture<DTO> get(String key) {
        final String finalUrl = endpointUrl + "/" + key;
        return CompletableFuture.supplyAsync(() -> {
            try {
                Request request = requestBuilder
                        .url(url + finalUrl)
                        .build();
                CompletableFuture<JsonElement> aFetch = fetch(request, "GET is called from: " + finalUrl);
                if(aFetch == null) throw new Exception("Fetching failed with GET: " + finalUrl);
                return Main.gson.fromJson(
                        aFetch.get(),
                        dtoType
                );
            }
            catch (Exception e) {
                return null;
            }
        });
    }

    public CompletableFuture<DTO> create(DTO dto) {
        RequestBody body = RequestBody.create(Main.gson.toJson(dto), JSON);
        return CompletableFuture.supplyAsync(() -> {
            try {
                Request request = requestBuilder
                        .url(url + endpointUrl)
                        .post(body)
                        .build();
                CompletableFuture<JsonElement> aFetch = fetch(request, "CREATE is called from: " + endpointUrl);
                if(aFetch == null) throw new Exception("Fetching failed with CREATE: " + endpointUrl);
                return Main.gson.fromJson(
                        aFetch.get(),
                        dtoType
                );
            }
            catch (Exception e) {
                return null;
            }
        });
    }

    public CompletableFuture<DTO> update(String key, UpdateDTO dto) {
        final String finalUrl = endpointUrl + "/" + key;
        RequestBody body = RequestBody.create(Main.gson.toJson(dto), JSON);
        return CompletableFuture.supplyAsync(() -> {
            try {
                Request request = requestBuilder
                        .url(url + finalUrl)
                        .post(body)
                        .build();
                CompletableFuture<JsonElement> aFetch = fetch(request, "UPDATE is called from: " + finalUrl);
                if(aFetch == null) throw new Exception("Fetching failed with UPDATE: " + finalUrl);
                return Main.gson.fromJson(
                        aFetch.get(),
                        dtoType
                );
            }
            catch (Exception e) {
                return null;
            }
        });
    }

    public CompletableFuture<DTO> delete(String key) {
        final String finalUrl = endpointUrl + "/" + key;
        return CompletableFuture.supplyAsync(() -> {
            try {
                Request request = requestBuilder
                        .url(url + finalUrl)
                        .delete()
                        .build();
                CompletableFuture<JsonElement> aFetch = fetch(request, "DELETE is called from: " + finalUrl);
                if(aFetch == null) throw new Exception("Fetching failed with DELETE" + finalUrl);
                return Main.gson.fromJson(
                        aFetch.get(),
                        dtoType
                );
            }
            catch (Exception e) {
                return null;
            }
        });
    }

    public static void connect(String url, String jwtKey) {
        FetchAPI.url = url;
        FetchAPI.requestBuilder = new Request.Builder()
                .addHeader(authHeader, "Bearer " + jwtKey);
    }

}
