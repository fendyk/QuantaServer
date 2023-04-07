package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.DTOs.LocationDTO;
import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.DTOs.SubscriptionRewardDTO;
import com.fendyk.DTOs.updates.UpdateMinecraftUserDTO;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.fetch.FetchMinecraftUser;
import com.fendyk.clients.redis.RedisMinecraftUser;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MinecraftUserAPI extends ClientAPI<FetchMinecraftUser, RedisMinecraftUser, UUID, MinecraftUserDTO> {

    public MinecraftUserAPI(API api, FetchMinecraftUser fetch, RedisMinecraftUser redis) {
        super(api, fetch, redis);
    }

    /**
     * Gets the player's balance
     * @param player
     * @return
     */
    @Nullable
    public BigDecimal getPlayerBalance(UUID player) {
        MinecraftUserDTO minecraftUser = get(player);
        if(minecraftUser == null) return null;
        return BigDecimal.valueOf(minecraftUser.getQuanta());
    }

    /**
     * Gets the player from either redis or db
     * @param player
     * @return
     */
    @Nullable
    public MinecraftUserDTO get(UUID player) {
        MinecraftUserDTO dto = redis.get(player);
        cachedRecords.put(player, dto);
        return dto;
    }

    /**
     * Returns the cached player, pure for UI/Visuals that require loads of updates only.
     * @param player
     * @return
     */
    public MinecraftUserDTO getCached(Player player) {
        return getCached(player.getUniqueId());
    }

    /**
     * Updates the player on both redis and db
     * @param player
     * @return
     */
    public MinecraftUserDTO update(UUID player, UpdateMinecraftUserDTO minecraftUserDTO) {
        MinecraftUserDTO dto = fetch.update(player, minecraftUserDTO);
        cachedRecords.put(player, dto);
        return dto;
    }

    public boolean withDrawBalance(OfflinePlayer player, BigDecimal amount) {
        UUID uuid = player.getUniqueId();
        MinecraftUserDTO minecraftUser = get(uuid);
        if(minecraftUser == null) return false;

        BigDecimal oldAmount = BigDecimal.valueOf(minecraftUser.getQuanta());
        BigDecimal newAmount = oldAmount.subtract(amount).setScale(2, RoundingMode.HALF_EVEN);

        if(oldAmount.compareTo(amount) < 0) {
            return false;
        }

        UpdateMinecraftUserDTO update = new UpdateMinecraftUserDTO();
        update.setQuanta(newAmount.floatValue());
        return update(uuid, update) != null;
    }

    /**
     * Deposits money into account of the user
     * @param player
     * @param amount
     * @return
     */
    public boolean depositBalance(OfflinePlayer player, BigDecimal amount) {
        UUID uuid = player.getUniqueId();
        MinecraftUserDTO minecraftUser = get(uuid);
        if(minecraftUser == null) return false;

        BigDecimal oldAmount = BigDecimal.valueOf(minecraftUser.getQuanta());
        BigDecimal newAmount = oldAmount.add(amount).setScale(2, RoundingMode.HALF_EVEN);

        UpdateMinecraftUserDTO update = new UpdateMinecraftUserDTO();
        update.setQuanta(newAmount.floatValue());
        return update(uuid, update) != null;
    }

    /**
     * Updates the last location of the player.
     * @param player
     * @param location
     * @return
     */
    public boolean updateLastLocation(Player player, Location location) {
        UpdateMinecraftUserDTO update = new UpdateMinecraftUserDTO();
        update.setLastLocation(new LocationDTO(location));
        return update(player.getUniqueId(), update) != null;
    }

}
