package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.clients.fetch.FetchMinecraftUser;
import com.fendyk.clients.redis.RedisMinecraftUser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

    public BigDecimal getPlayerBalance(UUID player) {
        JsonElement ePlayer = get(player);
        if(ePlayer == null || ePlayer.isJsonNull()) return null;
        return ePlayer.getAsJsonObject().get("quanta").getAsBigDecimal();
    }

    @Nullable
    public JsonElement get(UUID player) {
        return ObjectUtils.firstNonNull(redis.get(player), fetch.get(player));
    }

    public boolean update(UUID player, JsonObject data) {
        JsonObject result = fetch.update(player, data);
        boolean isCached = redis.set(player, data);
        return result != null && isCached;
    }

    public boolean withDrawBalance(UUID player, BigDecimal amount) {
        JsonElement ePlayer = get(player);
        if(ePlayer == null || ePlayer.isJsonNull()) return false;

        JsonObject jPlayer = ePlayer.getAsJsonObject();
        BigDecimal oldAmount = jPlayer.get("quanta").getAsBigDecimal();
        BigDecimal newAmount = oldAmount.subtract(amount).setScale(2, RoundingMode.HALF_EVEN);

        if(oldAmount.compareTo(amount) < 0) {
            return false;
        }

        jPlayer.addProperty("quanta", newAmount);

        return update(player, jPlayer);
    }

    public boolean depositBalance(UUID player, BigDecimal amount) {
        JsonElement ePlayer = get(player);
        if(ePlayer == null || ePlayer.isJsonNull()) return false;
        JsonObject jPlayer = ePlayer.getAsJsonObject();

        BigDecimal oldAmount = jPlayer.get("quanta").getAsBigDecimal();
        BigDecimal newAmount = oldAmount.add(amount).setScale(2, RoundingMode.HALF_EVEN);
        jPlayer.addProperty("quanta", newAmount);

        return update(player, jPlayer);
    }

}
