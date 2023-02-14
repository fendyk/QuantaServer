package com.fendyk.clients.fetch;

import com.fendyk.QuantaServer;
import com.fendyk.clients.FetchAPI;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class FetchLand extends FetchAPI<UUID> {

    public FetchLand(QuantaServer server, String url, boolean inDebugMode) {
        super(server, url, inDebugMode);
    }

    @Override
    public JsonElement get(UUID key) {
        Request request = new Request.Builder()
                .url(url + "/lands/" + key.toString())
                .get()
                .build();
        return fetchFromApi(request, "fetchLand");
    }

    @Override
    public JsonElement create(JsonObject data) {
        RequestBody body = RequestBody.create(data.toString(), JSON);
        Request request = new Request.Builder()
                .url(url + "/lands")
                .post(body)
                .build();
        return fetchFromApi(request, "fetchLand");
    }

    @Override
    public JsonElement update(UUID key, JsonObject data) {
        RequestBody body = RequestBody.create(data.toString(), JSON);
        Request request = new Request.Builder()
                .url(url + "/lands/" + key)
                .patch(body)
                .build();
        return fetchFromApi(request, "updateLand");
    }

    @Override
    public JsonElement delete(UUID key) {
        return null;
    }
}