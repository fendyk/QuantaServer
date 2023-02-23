package com.fendyk.clients.fetch;

import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.updates.UpdateChunkDTO;
import com.fendyk.QuantaServer;
import com.fendyk.clients.FetchAPI;
import com.fendyk.utilities.Vector2;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Objects;

public class FetchChunk extends FetchAPI<Vector2, ChunkDTO, UpdateChunkDTO> {
    public FetchChunk(QuantaServer server, String url, boolean inDebugMode) {
        super(server, url, inDebugMode);
    }

    @Override
    public ChunkDTO get(Vector2 key) {
        Request request = new Request.Builder()
                .url(url + "/chunks?x=" + key.getX() + "&z=" + key.getY())
                .get()
                .build();
        return QuantaServer.gson.fromJson(
                fetchFromApi(request, "getChunk"),
                ChunkDTO.class
        );
    }

    @Override
    public ChunkDTO create(ChunkDTO data) {
        RequestBody body = RequestBody.create(QuantaServer.gson.toJson(data), JSON);
        Request request = new Request.Builder()
                .url(url + "/chunks")
                .post(body)
                .build();
        return QuantaServer.gson.fromJson(
                fetchFromApi(request, "createChunk"),
                ChunkDTO.class
        );
    }

    @Override
    public ChunkDTO update(Vector2 key, UpdateChunkDTO data) {
        RequestBody body = RequestBody.create(QuantaServer.gson.toJson(data), JSON);
        Request request = new Request.Builder()
                .url(url + "/chunks?x=" + key.getX() + "&z=" + key.getY())
                .patch(body)
                .build();
        return QuantaServer.gson.fromJson(
                fetchFromApi(request, "updateChunk"),
                ChunkDTO.class
        );
    }

    @Override
    public ChunkDTO delete(Vector2 key) {
        return null;
    }
}
