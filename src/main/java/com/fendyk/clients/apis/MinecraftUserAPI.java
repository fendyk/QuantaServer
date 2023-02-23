package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.DTOs.MinecraftUserDTO;
import com.fendyk.clients.fetch.FetchMinecraftUser;
import com.fendyk.clients.redis.RedisMinecraftUser;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class MinecraftUserAPI {

    API api;
    FetchMinecraftUser fetch;
    RedisMinecraftUser redis;

    public MinecraftUserAPI(API api, FetchMinecraftUser fetch, RedisMinecraftUser redis) {
        this.api = api;
        this.fetch = fetch;
        this.redis = redis;
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
        return ObjectUtils.firstNonNull(redis.get(player), fetch.get(player));
    }

    /**
     * Updates the player on both redis and db
     * @param player
     * @param data
     * @return
     */
    public MinecraftUserDTO update(UUID player, MinecraftUserDTO minecraftUserDTO) {
        MinecraftUserDTO updatedMinecraftUserDTO = fetch.update(player, minecraftUserDTO);
        if(updatedMinecraftUserDTO == null) return null;
        boolean isCached = redis.set(player, updatedMinecraftUserDTO);
        return isCached ? updatedMinecraftUserDTO : null;
    }

    public boolean withDrawBalance(UUID player, BigDecimal amount) {
        MinecraftUserDTO minecraftUser = get(player);
        if(minecraftUser == null) return false;

        BigDecimal oldAmount = BigDecimal.valueOf(minecraftUser.getQuanta());
        BigDecimal newAmount = oldAmount.subtract(amount).setScale(2, RoundingMode.HALF_EVEN);

        if(oldAmount.compareTo(amount) < 0) {
            return false;
        }

        minecraftUser.setQuanta(newAmount.floatValue());

        return update(player, minecraftUser) != null;
    }

    public boolean depositBalance(UUID player, BigDecimal amount) {
        MinecraftUserDTO minecraftUser = get(player);
        if(minecraftUser == null) return false;

        BigDecimal oldAmount = BigDecimal.valueOf(minecraftUser.getQuanta());
        BigDecimal newAmount = oldAmount.add(amount).setScale(2, RoundingMode.HALF_EVEN);

        minecraftUser.setQuanta(newAmount.floatValue());

        return update(player, minecraftUser) != null;
    }

}
