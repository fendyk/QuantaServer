package com.fendyk.clients.apis;

import com.fendyk.QuantaServer;
import com.fendyk.clients.FetchAPI;
import com.google.gson.JsonObject;

import java.util.UUID;

public class LandAPI extends FetchAPI<UUID> {
    public LandAPI(QuantaServer server, String url, boolean inDebugMode) {
        super(server, url, inDebugMode);
    }

    @Override
    public JsonObject get(UUID key) {
        return null;
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
