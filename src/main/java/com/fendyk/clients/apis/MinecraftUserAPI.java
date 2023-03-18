package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.DTOs.updates.UpdateMinecraftUserDTO;
import com.fendyk.Main;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.fetch.FetchMinecraftUser;
import com.fendyk.clients.redis.RedisMinecraftUser;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class MinecraftUserAPI extends ClientAPI<FetchMinecraftUser, RedisMinecraftUser> {

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
        Bukkit.getLogger().info("api: " +player.toString());
        return redis.get(player);
    }

    /**
     * Updates the player on both redis and db
     * @param player
     * @return
     */
    public MinecraftUserDTO update(UUID player, UpdateMinecraftUserDTO minecraftUserDTO) {
        return fetch.update(player, minecraftUserDTO);
    }

    public boolean withDrawBalance(UUID player, BigDecimal amount) {
        MinecraftUserDTO minecraftUser = get(player);
        if(minecraftUser == null) return false;

        BigDecimal oldAmount = BigDecimal.valueOf(minecraftUser.getQuanta());
        BigDecimal newAmount = oldAmount.subtract(amount).setScale(2, RoundingMode.HALF_EVEN);

        if(oldAmount.compareTo(amount) < 0) {
            return false;
        }

        UpdateMinecraftUserDTO update = new UpdateMinecraftUserDTO();
        update.setQuanta(newAmount.floatValue());
        return update(player, update) != null;
    }

    /**
     * Deposits money into account of the user
     * @param player
     * @param amount
     * @return
     */
    public boolean depositBalance(UUID player, BigDecimal amount) {
        MinecraftUserDTO minecraftUser = get(player);
        if(minecraftUser == null) return false;

        BigDecimal oldAmount = BigDecimal.valueOf(minecraftUser.getQuanta());
        BigDecimal newAmount = oldAmount.add(amount).setScale(2, RoundingMode.HALF_EVEN);

        UpdateMinecraftUserDTO update = new UpdateMinecraftUserDTO();
        update.setQuanta(newAmount.floatValue());
        return update(player, update) != null;
    }

}
