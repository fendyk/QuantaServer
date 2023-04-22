package com.fendyk.clients.fetch

import com.fendyk.DTOs.ActivitiesDTO
import com.fendyk.DTOs.ChunkDTO
import com.fendyk.DTOs.updates.UpdateActivitiesDTO
import com.fendyk.DTOs.updates.UpdateChunkDTO
import com.fendyk.Main
import com.fendyk.clients.FetchAPI
import com.fendyk.utilities.Vector2
import okhttp3.Request
import okhttp3.RequestBody
import java.util.*
import java.util.concurrent.CompletableFuture

class FetchActivities : FetchAPI<UUID, ActivitiesDTO, UpdateActivitiesDTO>(ActivitiesDTO::class.java) {
    override fun update(key: UUID, dto: UpdateActivitiesDTO): CompletableFuture<ActivitiesDTO> {
        return fetch("/chunks/$key", RequestMethod.PATCH, dto)
    }

}
