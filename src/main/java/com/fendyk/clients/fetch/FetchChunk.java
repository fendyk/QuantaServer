package com.fendyk.clients.fetch;

import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.updates.UpdateChunkDTO;
import com.fendyk.Main;
import com.fendyk.clients.FetchAPI;
import com.fendyk.utilities.Vector2;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FetchChunk extends FetchAPI<Vector2, ChunkDTO, UpdateChunkDTO> {
    public FetchChunk(Main server, String url, boolean inDebugMode, String apiKey) {
        super(server, url, inDebugMode, apiKey);
    }

    @Override
    public ChunkDTO get(Vector2 key) {
        Request request = this.requestBuilder
                .url(url + "/chunks/" + key.getX() + "/" + key.getY())
                .get()
                .build();
        return Main.gson.fromJson(
                fetchFromApi(request, "getChunk"),
                ChunkDTO.class
        );
    }

    @Override
    public ChunkDTO create(ChunkDTO data) {
        RequestBody body = RequestBody.create(Main.gson.toJson(data), JSON);
        Request request = this.requestBuilder
                .url(url + "/chunks")
                .post(body)
                .build();
        return Main.gson.fromJson(
                fetchFromApi(request, "createChunk"),
                ChunkDTO.class
        );
    }

    @Override
    public ChunkDTO update(Vector2 key, UpdateChunkDTO data) {
        RequestBody body = RequestBody.create(Main.gson.toJson(data), JSON);
        Request request = this.requestBuilder
                .url(url + "/chunks/" + key.getX() + "/" + key.getY())
                .patch(body)
                .build();
        return Main.gson.fromJson(
                fetchFromApi(request, "updateChunk"),
                ChunkDTO.class
        );
    }

    @Override
    public ChunkDTO delete(Vector2 key) {
        return null;
    }
}
