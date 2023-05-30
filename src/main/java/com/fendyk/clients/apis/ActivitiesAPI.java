package com.fendyk.clients.apis;

import com.fendyk.DTOs.ActivitiesDTO;
import com.fendyk.DTOs.updates.UpdateActivitiesDTO;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.fetch.FetchActivities;
import com.fendyk.clients.redis.RedisActivities;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ActivitiesAPI extends ClientAPI<FetchActivities, RedisActivities, UUID, ActivitiesDTO> {

    public ActivitiesAPI(FetchActivities fetch, RedisActivities redis) {
        super(fetch, redis);
    }

    public ActivitiesDTO get(Player player) {
        UUID uuid = player.getUniqueId();
        CompletableFuture<ActivitiesDTO> aFuture = redis.get(String.valueOf(uuid));
        ActivitiesDTO activitiesDTO = aFuture.join();
        cachedRecords.put(player.getUniqueId(), activitiesDTO);
        return activitiesDTO;
    }

    /**
     * Updates the user's new activities.
     * @param player
     * @param updated
     * @return
     */
    public ActivitiesDTO update(Player player, UpdateActivitiesDTO updated) {
        CompletableFuture<ActivitiesDTO> aUpdated = fetch.update(String.valueOf(player.getUniqueId()), updated);
        return aUpdated.join();
    }

}