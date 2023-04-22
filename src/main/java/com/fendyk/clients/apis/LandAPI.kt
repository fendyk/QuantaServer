package com.fendyk.clients.apis

import com.fendyk.API
import com.fendyk.DTOs.LandDTO
import com.fendyk.DTOs.TaggedLocationDTO
import com.fendyk.Main
import com.fendyk.clients.ClientAPI
import com.fendyk.clients.fetch.FetchLand
import com.fendyk.clients.redis.RedisLand
import net.luckperms.api.model.user.User
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Player
import org.joda.time.DateTime
import java.util.*
import java.util.concurrent.CompletableFuture

class LandAPI(fetch: FetchLand, redis: RedisLand) : ClientAPI<FetchLand, RedisLand, String, LandDTO>(fetch, redis) {
    companion object {
        var main: Main = Main.getInstance()
        var api: API = main.api
    }

    /**
     * Creates a new land. Requires an existing chunk
     * @param owner
     * @param name
     * @param chunk
     * @return The new land if created or null if something went wrong.
     * @throws Exception When something goes wrong lol
     */
    fun create(owner: Player, name: String, chunk: Chunk, location: Location): CompletableFuture<LandDTO> {
        return CompletableFuture.supplyAsync {
            val uuid = owner.uniqueId
            val user: User? = Main.getInstance().luckPermsApi.userManager.getUser(uuid)

            /* Get references */
            val chunkAPI: ChunkAPI = api.chunkAPI

            /* Get chunk */
            var chunkDTO = chunkAPI[chunk]

            /* If we cannot find one, create new */
            if (chunkDTO == null) { //TODO: Make async
                chunkDTO = chunkAPI.create(chunk, false)
                if (chunkDTO == null) throw Exception("Could not create chunk when creating land")
            }
            val taggedLocationDTO = TaggedLocationDTO("spawn", location)

            /* Create the actual land */
            val landDTO = LandDTO()
            landDTO.name = name
            landDTO.ownerId = owner.uniqueId.toString()
            landDTO.homes.add(taggedLocationDTO)

            val res: CompletableFuture<LandDTO> = fetch.create(landDTO)
            val newLandDTO: LandDTO = res.get()

            val expireDate = DateTime()
            expireDate.plusMinutes(2)
            val primaryGroup = user?.primaryGroup
            val canExpire = primaryGroup.equals("default", ignoreCase = true) || primaryGroup.equals("barbarian", ignoreCase = true)

            /* Claim the chunk */chunkAPI.claim(chunk, newLandDTO.id, canExpire, if (canExpire) expireDate else null)
            cachedRecords[newLandDTO.id] = newLandDTO
            return@supplyAsync newLandDTO
        }
    }

    /**
     * Gets the land via land's id
     * @param id
     * @return LandDTO or null if not found
     */
    operator fun get(id: String): CompletableFuture<LandDTO> {
        return CompletableFuture.supplyAsync {
            val res: CompletableFuture<LandDTO> = redis.get(id)
            val dto = res.get()
            cachedRecords[id] = dto
            return@supplyAsync dto
        }
    }

    /**
     * Gets the land via player's uuid
     * @param player
     * @return LandDTO or null if not found
     */
    operator fun get(player: UUID): CompletableFuture<LandDTO> {
        return CompletableFuture.supplyAsync {
            val res: CompletableFuture<LandDTO> = redis.get(player.toString())
            val dto = res.get()
            cachedRecords[dto.id] = dto
            return@supplyAsync dto
        }
    }
}