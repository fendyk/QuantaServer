package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.updates.UpdateActivitiesDTO;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.fetch.FetchActivities;
import com.fendyk.clients.redis.RedisActivities;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ActivitiesAPI extends ClientAPI<FetchActivities, RedisActivities, UUID, ActivitiesDTO> {

    public ActivitiesAPI(API api, FetchActivities fetch, RedisActivities redis) {
        super(api, fetch, redis);
    }

    public ActivitiesDTO get(Player player) throws ExecutionException, InterruptedException {
        UUID uuid = player.getUniqueId();
        CompletableFuture<ActivitiesDTO> aActivitiesDTO = redis.get(String.valueOf(uuid));
        ActivitiesDTO activitiesDTO = aActivitiesDTO.get();
        cachedRecords.put(player.getUniqueId(), aActivitiesDTO.get());
        return activitiesDTO;
    }

    /**
     * Updates the user's new activities.
     * @param player
     * @param updated
     * @return
     */
    public ActivitiesDTO update(Player player, UpdateActivitiesDTO updated) throws ExecutionException, InterruptedException {
        CompletableFuture<ActivitiesDTO> aUpdated = fetch.update(String.valueOf(player.getUniqueId()), updated);
        return aUpdated.get();
    }

}