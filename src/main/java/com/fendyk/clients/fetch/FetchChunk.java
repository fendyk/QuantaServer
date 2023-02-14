package com.fendyk.clients.fetch;

import com.fendyk.QuantaServer;
import com.fendyk.clients.FetchAPI;
import com.fendyk.utilities.Vector2;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Objects;

public class FetchChunk extends FetchAPI<Vector2> {
    public FetchChunk(QuantaServer server, String url, boolean inDebugMode) {
        super(server, url, inDebugMode);
    }

    @Override
    public JsonElement get(Vector2 key) {
        Request request = new Request.Builder()
                .url(url + "/chunks?x=" + key.getX() + "&z=" + key.getY())
                .get()
                .build();
        return fetchFromApi(request, "getChunk");
    }

    @Override
    public JsonElement create(JsonObject data) {
        RequestBody body = RequestBody.create(data.toString(), JSON);
        Request request = new Request.Builder()
                .url(url + "/chunks")
                .post(body)
                .build();
        return fetchFromApi(request, "createChunk");
    }

    @Override
    public JsonElement update(Vector2 key, JsonObject data) {
        RequestBody body = RequestBody.create(data.toString(), JSON);
        Request request = new Request.Builder()
                .url(url + "/chunks?x=" + key.getX() + "&z=" + key.getY())
                .patch(body)
                .build();
        return fetchFromApi(request, "updateChunk");
    }

    @Override
    public JsonElement delete(Vector2 key) {
        return null;
    }
}
