package com.fendyk.clients.apis

import com.fendyk.API
import com.fendyk.DTOs.ChunkDTO
import com.fendyk.DTOs.LandDTO
import com.fendyk.DTOs.TaggedLocationDTO
import com.fendyk.Main
import com.fendyk.clients.ClientAPI
import com.fendyk.clients.fetch.FetchLand
import com.fendyk.clients.redis.RedisLand
import com.fendyk.utilities.Vector2
import net.luckperms.api.model.user.User
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.entity.Player
import org.joda.time.DateTime
import java.util.*
import java.util.concurrent.CompletableFuture

class LandAPI(fetch: FetchLand, redis: RedisLand) : ClientAPI<FetchLand, RedisLand, String, LandDTO>(fetch, redis) {
    companion object {
        var main: Main = Main.instance
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
    fun create(owner: Player, name: String, chunk: Chunk, location: Location): CompletableFuture<LandDTO?> {
        return CompletableFuture.supplyAsync {
            val uuid = owner.uniqueId
            val user: User? = Main.instance.luckPermsApi?.userManager?.getUser(uuid)

            // Find chunk
            var chunkDTO: ChunkDTO? = api.chunkAPI.redis.get(Vector2(chunk.x, chunk.z)).join()

            /* If we cannot find one, create new */
            if (chunkDTO == null) {
                chunkDTO = api.chunkAPI.fetch.create(ChunkDTO(chunk.x, chunk.z)).get()
                        ?: throw Exception("Could not create chunk when trying to creating land")
            }

            val taggedLocationDTO = TaggedLocationDTO("spawn", location)

            /* Create the actual land */
            val newLandDTO = LandDTO()
            newLandDTO.name = name
            newLandDTO.ownerId = owner.uniqueId.toString()
            newLandDTO.homes.add(taggedLocationDTO)

            val createdLandDTO: LandDTO = fetch.create(newLandDTO).join() ?: throw Exception("Could not create land")

            val expireDate = DateTime()
            expireDate.plusMinutes(2)
            val primaryGroup = user?.primaryGroup
            val canExpire: Boolean = primaryGroup.equals("default", ignoreCase = true) || primaryGroup.equals("barbarian", ignoreCase = true)

            /* Claim the chunk */
            val isClaimed: Boolean = api.chunkAPI.claim(chunk, newLandDTO.id, canExpire, if (canExpire) expireDate else null).join()
                    ?: throw Exception("Could not claim the chunk you're standing in.")
            cachedRecords[newLandDTO.id] = newLandDTO
            return@supplyAsync newLandDTO
        }
    }

    /**
     * Gets the land via land's id
     * @param id
     * @return LandDTO or null if not found
     */
    fun get(id: String): CompletableFuture<LandDTO?> {
        return CompletableFuture.supplyAsync {
            val landDTO: LandDTO? = redis.get(id).join()
            if (landDTO != null) cachedRecords[id] = landDTO
            return@supplyAsync landDTO
        }
    }

    /**
     * Gets the land via player's uuid
     * @param player
     * @return LandDTO or null if not found
     */
    fun get(player: UUID): CompletableFuture<LandDTO?> {
        return CompletableFuture.supplyAsync {
            val landDTO: LandDTO? = redis.get(player.toString()).join()
            if (landDTO != null) cachedRecords[landDTO.id] = landDTO
            return@supplyAsync landDTO
        }
    }
}