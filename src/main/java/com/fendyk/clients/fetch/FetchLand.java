package com.fendyk.clients.fetch;

import com.fendyk.DTOs.LandDTO;
import com.fendyk.DTOs.updates.UpdateLandDTO;
import com.fendyk.Main;
import com.fendyk.clients.FetchAPI;
import okhttp3.Request;
import okhttp3.RequestBody;
import java.util.UUID;

public class FetchLand extends FetchAPI<UUID, LandDTO, UpdateLandDTO> {

    public FetchLand(Main server, String url, boolean inDebugMode, String apiKey) {
        super(server, url, inDebugMode, apiKey);
    }

    @Override
    public LandDTO get(UUID key) {
        Request request = this.requestBuilder
                .url(url + "/lands/" + key.toString())
                .get()
                .build();
        return Main.gson.fromJson(
                fetchFromApi(request, "fetchLandByOwner"),
                LandDTO.class
        );
    }

    public LandDTO get(String key) {
        Request request = this.requestBuilder
                .url(url + "/lands/" + key)
                .get()
                .build();
        return Main.gson.fromJson(
                fetchFromApi(request, "fetchLandByKey"),
                LandDTO.class
        );
    }

    @Override
    public LandDTO create(LandDTO data) {
        RequestBody body = RequestBody.create(Main.gson.toJson(data), JSON);
        Request request = this.requestBuilder
                .url(url + "/lands")
                .post(body)
                .build();
        return Main.gson.fromJson(
                fetchFromApi(request, "createLand"),
                LandDTO.class
        );
    }

    @Override
    public LandDTO update(UUID key, UpdateLandDTO data) {
        RequestBody body = RequestBody.create(data.toString(), JSON);
        Request request = this.requestBuilder
                .url(url + "/lands/" + key)
                .patch(body)
                .build();
        return Main.gson.fromJson(
                fetchFromApi(request, "updateLand"),
                LandDTO.class
        );
    }

    @Override
    public LandDTO delete(UUID key) {
        return null;
    }
}