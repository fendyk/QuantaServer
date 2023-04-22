package com.fendyk.clients.fetch;

import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.updates.UpdateActivitiesDTO;
import com.fendyk.Main;
import com.fendyk.clients.FetchAPI;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.UUID;

public class FetchActivities extends FetchAPI<UUID, ActivitiesDTO, UpdateActivitiesDTO> {
    public FetchActivities(Main server, String url, boolean inDebugMode, String apiKey) {
        super(server, url, inDebugMode, apiKey);
    }

    @Override
    public ActivitiesDTO get(UUID key) {
        return null;
    }

    @Override
    public ActivitiesDTO create(ActivitiesDTO data) {
        return null;
    }

    @Override
    public ActivitiesDTO update(UUID key, UpdateActivitiesDTO data) {
        RequestBody body = RequestBody.create(Main.gson.toJson(data), JSON);
        Request request = this.requestBuilder
                .url(url + "/activities/" + key.toString())
                .patch(body)
                .build();
        return Main.gson.fromJson(
                fetchFromApi(request, "updateActivities"),
                ActivitiesDTO.class
        );
    }

    @Override
    public ActivitiesDTO delete(UUID key) {
        return null;
    }
}

