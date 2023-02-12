package com.fendyk.clients.fetch;

import com.fendyk.QuantaServer;
import com.fendyk.clients.FetchAPI;
import com.google.gson.JsonObject;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.UUID;

public class FetchMinecraftUser extends FetchAPI<UUID> {


    public FetchMinecraftUser(QuantaServer server, String url, boolean inDebugMode) {
        super(server, url, inDebugMode);
    }

    @Override
    public JsonObject get(UUID key) {
        Request request = new Request.Builder()
                .url(url + "/minecraftusers/" + key.toString())
                .get()
                .build();
        return fetchFromApi(request, "fetchMinecraftUser");
    }

    @Override
    public JsonObject create(JsonObject data) {
        return null;
    }

    @Override
    public JsonObject update(UUID key, JsonObject data) {
        RequestBody body = RequestBody.create(data.toString(), JSON);
        Request request = new Request.Builder()
                .url(url + "/minecraftusers/" + key)
                .patch(body)
                .build();
        return fetchFromApi(request, "updateMinecraftUser");
    }

    @Override
    public JsonObject delete(UUID any) {
        return null;
    }

}
