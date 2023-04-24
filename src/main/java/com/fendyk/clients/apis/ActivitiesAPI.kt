package com.fendyk.clients.apis

import com.fendyk.API
import com.fendyk.DTOs.ActivitiesDTO
import com.fendyk.DTOs.updates.UpdateActivitiesDTO
import com.fendyk.clients.ClientAPI
import com.fendyk.clients.fetch.FetchActivities
import com.fendyk.clients.redis.RedisActivities
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

class ActivitiesAPI(fetch: FetchActivities, redis: RedisActivities) :
        ClientAPI<FetchActivities, RedisActivities, UUID, ActivitiesDTO?>(fetch, redis) {

     fun get(player: Player): CompletableFuture<ActivitiesDTO?> {
        return CompletableFuture.supplyAsync {
            val uuid = player.uniqueId
            val activitiesDTO: ActivitiesDTO? = redis.get(uuid).get()
            cachedRecords[uuid] = activitiesDTO
            return@supplyAsync activitiesDTO
        }
    }

    /**
     * Updates the user's new activities.
     * @param player
     * @param updated
     * @return
     */
    fun update(player: Player, updated: UpdateActivitiesDTO): CompletableFuture<ActivitiesDTO?> {
        return fetch.update(player.uniqueId, updated)
    }
}