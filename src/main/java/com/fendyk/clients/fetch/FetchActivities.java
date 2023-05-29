package com.fendyk.clients.fetch;

import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.DTOs.updates.UpdateActivitiesDTO;
import com.fendyk.Main;
import com.fendyk.clients.FetchAPI;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.UUID;

public class FetchActivities extends FetchAPI<ActivitiesDTO, UpdateActivitiesDTO> {
    public FetchActivities(String url) {
        super(url, ActivitiesDTO.class);
    }
}