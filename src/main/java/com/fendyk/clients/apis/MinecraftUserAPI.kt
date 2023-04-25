package com.fendyk.clients.apis

import com.fendyk.DTOs.LocationDTO
import com.fendyk.DTOs.MinecraftUserDTO
import com.fendyk.DTOs.updates.UpdateMinecraftUserDTO
import com.fendyk.clients.ClientAPI
import com.fendyk.clients.fetch.FetchMinecraftUser
import com.fendyk.clients.redis.RedisMinecraftUser
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

class MinecraftUserAPI(fetch: FetchMinecraftUser, redis: RedisMinecraftUser) :
        ClientAPI<FetchMinecraftUser, RedisMinecraftUser, UUID?, MinecraftUserDTO?>(fetch, redis) {
    /**
     * Gets the player's balance
     * @param player
     * @return
     */
    fun getPlayerBalance(player: OfflinePlayer): CompletableFuture<Double> {
        return CompletableFuture.supplyAsync {
            val awaitMinecraftUser = get(player.uniqueId)
            return@supplyAsync awaitMinecraftUser.join()?.quanta
        }
    }

    /**
     * Gets the player's balance
     * @param player
     * @return
     */
    fun hasEnoughBalance(player: OfflinePlayer, amount: Double): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            val minecraftUser = get(player.uniqueId).join()
            return@supplyAsync minecraftUser?.quanta != null && minecraftUser.quanta > amount
        }
    }

    /**
     * Gets the player from either redis or db
     * @param player
     * @return
     */
    fun get(player: UUID): CompletableFuture<MinecraftUserDTO?> {
        return CompletableFuture.supplyAsync {
            val minecraftUser = redis.get(player).join()
            cachedRecords[player] = minecraftUser
            return@supplyAsync minecraftUser
        }
    }

    /**
     * Returns the cached player, pure for UI/Visuals that require loads of updates only.
     * @param player
     * @return
     */
    fun getCached(player: Player): MinecraftUserDTO? {
        return getCached(player.uniqueId)
    }

    /**
     * Updates the player on both redis and db
     * @param player
     * @return
     */
    fun update(player: UUID, minecraftUserDTO: UpdateMinecraftUserDTO): CompletableFuture<MinecraftUserDTO?> {
        return CompletableFuture.supplyAsync {
            val minecraftUser: MinecraftUserDTO = fetch.update(player, minecraftUserDTO).join()
                    ?: throw Exception("Could not fetch your minecraft user")
            cachedRecords[player] = minecraftUser
            return@supplyAsync minecraftUser
        }
    }

    fun withDrawBalance(player: OfflinePlayer, amount: Double): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            val uuid: UUID = player.uniqueId
            val minecraftUser: MinecraftUserDTO? = get(uuid).join()

            val oldAmount: Double = minecraftUser?.quanta ?: 0.0
            val newAmount: Double = oldAmount - amount

            if (newAmount < 0) throw Exception("you cannot withdraw money that does not exist!")

            val updateDTO = UpdateMinecraftUserDTO()
            updateDTO.quanta = newAmount

            return@supplyAsync update(uuid, updateDTO).handleAsync<Boolean> { result: MinecraftUserDTO?, ex: Throwable? ->
                ex == null && result != null
            }.join()
        }
    }

    /**
     * Deposits money into account of the user
     * @param player
     * @param amount
     * @return
     */
    fun depositBalance(player: OfflinePlayer, amount: Double): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            val uuid: UUID = player.uniqueId
            val minecraftUser: MinecraftUserDTO? = get(uuid).join()

            val oldAmount: Double = minecraftUser?.quanta ?: 0.0
            val newAmount: Double = oldAmount + amount
            val updateDTO = UpdateMinecraftUserDTO()
            updateDTO.quanta = newAmount
            return@supplyAsync update(uuid, updateDTO).handleAsync<Boolean> { result: MinecraftUserDTO?, ex: Throwable? ->
                ex == null && result != null
            }.join()
        }
    }

    /**
     * Updates the last location of the player.
     * @param player
     * @param location
     * @return
     */
    fun updateLastLocation(player: Player, location: Location): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            val updateDTO = UpdateMinecraftUserDTO()
            updateDTO.lastLocation = LocationDTO(location)
            return@supplyAsync update(player.uniqueId, updateDTO).handleAsync<Boolean> { result: MinecraftUserDTO?, ex: Throwable? ->
                ex == null && result != null
            }.join()
        }
    }
}
