package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.updates.UpdateActivitiesDTO;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.fetch.FetchActivities;
import com.fendyk.clients.redis.RedisActivities;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ActivitiesAPI extends ClientAPI<FetchActivities, RedisActivities> {

    public ActivitiesAPI(API api, FetchActivities fetch, RedisActivities redis) {
        super(api, fetch, redis);
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