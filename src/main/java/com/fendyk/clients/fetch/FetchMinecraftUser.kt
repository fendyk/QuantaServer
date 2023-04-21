package com.fendyk.clients.fetch

import com.fendyk.DTOs.MinecraftUserDTO
import com.fendyk.DTOs.updates.UpdateActivitiesDTO
import com.fendyk.DTOs.updates.UpdateMinecraftUserDTO
import com.fendyk.clients.FetchAPI
import java.util.*
import java.util.concurrent.CompletableFuture

class FetchMinecraftUser : FetchAPI<UUID, MinecraftUserDTO, UpdateMinecraftUserDTO>(MinecraftUserDTO::class.java) {
    override fun get(key: UUID): CompletableFuture<MinecraftUserDTO> {
        return fetch("/minecraftusers/$key", RequestMethod.GET, null)
    }

    override fun update(key: UUID, data: UpdateMinecraftUserDTO): CompletableFuture<MinecraftUserDTO> {
        return fetch("/minecraftusers/$key", RequestMethod.PATCH, data)
    }
}
