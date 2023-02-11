package com.fendyk.clients.apis;

import com.fendyk.clients.fetch.FetchMinecraftUser;
import com.fendyk.clients.redis.RedisMinecraftUser;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class MinecraftUserAPI {

    FetchMinecraftUser fetch;
    RedisMinecraftUser redis;

    public MinecraftUserAPI(FetchMinecraftUser fetch, RedisMinecraftUser redis) {
        super();
        this.fetch = fetch;
        this.redis = redis;
    }

    public BigDecimal getPlayerBalance(UUID player) {
        JsonObject jPlayer = redis.get(player);
        jPlayer = jPlayer != null ? jPlayer : fetch.get(player);

        return jPlayer.get("quanta").getAsBigDecimal();
    }

    public JsonObject get(UUID player) {
        JsonObject json = redis.get(player);
        return json != null ? json : fetch.get(player);
    }

    public boolean update(UUID player, JsonObject data) {
        JsonObject result = fetch.update(player, data);
        boolean isCached = redis.set(player, data);
        return !result.isJsonNull() && isCached;
    }

    public boolean withDrawBalance(UUID player, BigDecimal amount) {
        JsonObject jPlayer = get(player);
        BigDecimal oldAmount = jPlayer.get("quanta").getAsBigDecimal();
        BigDecimal newAmount = oldAmount.subtract(amount).setScale(2, RoundingMode.HALF_EVEN);

        if(oldAmount.compareTo(amount) < 0) {
            return false;
        }

        jPlayer.addProperty("quanta", newAmount);

        return update(player, jPlayer);
    }

    public boolean depositBalance(UUID player, BigDecimal amount) {
        JsonObject jPlayer = get(player);

        BigDecimal oldAmount = jPlayer.get("quanta").getAsBigDecimal();
        BigDecimal newAmount = oldAmount.add(amount).setScale(2, RoundingMode.HALF_EVEN);
        jPlayer.addProperty("quanta", newAmount);

        return update(player, jPlayer);
    }

}
