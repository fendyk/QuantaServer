package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.updates.UpdateActivitiesDTO;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.fetch.FetchActivities;
import com.fendyk.clients.redis.RedisActivities;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ActivitiesAPI extends ClientAPI<FetchActivities, RedisActivities, UUID, ActivitiesDTO> {

    public ActivitiesAPI(API api, FetchActivities fetch, RedisActivities redis) {
        super(api, fetch, redis);
    }

    public ActivitiesDTO get(Player player) {
        UUID uuid = player.getUniqueId();
        ActivitiesDTO dto = redis.get(uuid);
        cachedRecords.put(player.getUniqueId(), dto);
        return dto;
    }

    /**
     * Updates the user's new activities.
     * @param player
     * @param updated
     * @return
     */
    public ActivitiesDTO update(Player player, UpdateActivitiesDTO updated) {
        return fetch.update(player.getUniqueId(), updated);
    }

}