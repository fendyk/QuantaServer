package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.updates.UpdateActivitiesDTO;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.fetch.FetchActivities;

import java.util.UUID;

public class ActivitiesAPI extends ClientAPI<FetchActivities, Class<?>> {

    public ActivitiesAPI(API api, FetchActivities fetch, Class<?> redis) {
        super(api, fetch, null);
    }

    public ActivitiesDTO update(UUID player, UpdateActivitiesDTO activitiesDTO) {
        return this.fetch.update(player, activitiesDTO);
    }

}