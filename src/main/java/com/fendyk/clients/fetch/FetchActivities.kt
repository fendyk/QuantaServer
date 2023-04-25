package com.fendyk.clients.fetch

import com.fendyk.DTOs.ActivitiesDTO
import com.fendyk.DTOs.updates.UpdateActivitiesDTO
import com.fendyk.clients.FetchAPI
import java.util.*
import java.util.concurrent.CompletableFuture

class FetchActivities : FetchAPI<UUID, ActivitiesDTO, UpdateActivitiesDTO>(ActivitiesDTO::class.java) {
    override fun update(key: UUID, dto: UpdateActivitiesDTO): CompletableFuture<ActivitiesDTO?> {
        return fetch("/chunks/$key", RequestMethod.PATCH, dto)
    }

}
