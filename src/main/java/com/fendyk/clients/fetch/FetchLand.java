package com.fendyk.clients.fetch;

import com.fendyk.QuantaServer;
import com.fendyk.clients.FetchAPI;
import com.google.gson.JsonObject;
import okhttp3.Request;

import java.util.UUID;

public class FetchLand extends FetchAPI<UUID> {


    public FetchLand(QuantaServer server, String url, boolean inDebugMode) {
        super(server, url, inDebugMode);
    }

    @Override
    public JsonObject get(UUID key) {
        Request request = new Request.Builder()
                .url(url + "/lands/" + key.toString())
                .get()
                .build();
        return fetchFromApi(request, "fetchLand");
    }

    @Override
    public JsonObject create(JsonObject data) {
        return null;
    }

    @Override
    public JsonObject update(UUID key, JsonObject data) {
        return null;
    }

    @Override
    public JsonObject delete(UUID key) {
        return null;
    }
}
