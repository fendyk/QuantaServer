package com.fendyk.clients.fetch;

import com.fendyk.DTOs.LandDTO;
import com.fendyk.Main;
import com.fendyk.clients.FetchAPI;
import okhttp3.Request;
import okhttp3.RequestBody;
import java.util.UUID;

public class FetchLand extends FetchAPI<UUID, LandDTO, LandDTO> {

    public FetchLand(Main server, String url, boolean inDebugMode) {
        super(server, url, inDebugMode);
    }

    @Override
    public LandDTO get(UUID key) {
        Request request = new Request.Builder()
                .url(url + "/lands/" + key.toString())
                .get()
                .build();
        return Main.gson.fromJson(
                fetchFromApi(request, "fetchLand"),
                LandDTO.class
        );
    }

    @Override
    public LandDTO create(LandDTO data) {
        RequestBody body = RequestBody.create(data.toString(), JSON);
        Request request = new Request.Builder()
                .url(url + "/lands")
                .post(body)
                .build();
        return Main.gson.fromJson(
                fetchFromApi(request, "createLand"),
                LandDTO.class
        );
    }

    @Override
    public LandDTO update(UUID key, LandDTO data) {
        RequestBody body = RequestBody.create(data.toString(), JSON);
        Request request = new Request.Builder()
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