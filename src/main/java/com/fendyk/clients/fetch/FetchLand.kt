package com.fendyk.clients.fetch

import com.fendyk.DTOs.LandDTO
import com.fendyk.DTOs.updates.UpdateLandDTO
import com.fendyk.clients.FetchAPI
import java.util.concurrent.CompletableFuture

class FetchLand : FetchAPI<String, LandDTO, UpdateLandDTO>(LandDTO::class.java) {
    override operator fun get(key: String): CompletableFuture<LandDTO> {
        return fetch("/lands/$key", RequestMethod.GET, null)
    }

    override fun create(dto: LandDTO): CompletableFuture<LandDTO> {
        return fetch("/lands", RequestMethod.POST, dto)
    }

    override fun update(key: String, dto: UpdateLandDTO): CompletableFuture<LandDTO> {
        return fetch("/lands/$key", RequestMethod.PATCH, dto)
    }
}