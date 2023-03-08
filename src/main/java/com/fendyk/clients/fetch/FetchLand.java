package com.fendyk.clients.fetch;

import com.fendyk.DTOs.LandDTO;
import com.fendyk.DTOs.updates.UpdateLandDTO;
import com.fendyk.Main;
import com.fendyk.clients.FetchAPI;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.bukkit.Bukkit;

import java.util.UUID;

public class FetchLand extends FetchAPI<String, LandDTO, UpdateLandDTO> {

    public FetchLand(Main server, String url, boolean inDebugMode, String apiKey) {
        super(server, url, inDebugMode, apiKey);
    }

    @Override
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
    public LandDTO update(String key, UpdateLandDTO data) {
        Bukkit.getLogger().info(Main.gson.toJson(data));
        RequestBody body = RequestBody.create(Main.gson.toJson(data), JSON);
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
    public LandDTO delete(String key) {
        return null;
    }
}