package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.clients.ApiClient;
import com.fendyk.clients.fetch.FetchChunk;
import com.fendyk.clients.redis.RedisChunk;
import com.fendyk.utilities.Vector2;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import io.netty.util.internal.ObjectUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.Chunk;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ChunkAPI extends ApiClient {

    API api;
    FetchChunk fetch;
    RedisChunk redis;

    public ChunkAPI(API api, FetchChunk fetch, RedisChunk redis) {
        this.api = api;
        this.fetch = fetch;
        this.redis = redis;
    }

    @Nullable
    public JsonElement get(Chunk chunk) {
        Vector2 chunkPos = new Vector2(chunk.getX(), chunk.getZ());
        return ObjectUtils.firstNonNull(redis.get(chunkPos), fetch.get(chunkPos));
    }

    public JsonElement create(Chunk chunk, boolean isNonClaimable) {
        JsonObject data = new JsonObject();
        data.addProperty("isNonClaimable", isNonClaimable);
        data.addProperty("xCoord", chunk.getX());
        data.addProperty("zCoord", chunk.getZ());
        JsonElement result = fetch.create(data);

        if(result == null || result.isJsonNull()) return result;

        boolean isCached = redis.set(new Vector2(chunk.getX(), chunk.getZ()), result.getAsJsonObject());
        return !result.isJsonNull() && isCached ? result : null;
    }

    public JsonElement update(Chunk chunk, JsonObject data) {
        Vector2 vector2 = new Vector2(chunk.getX(), chunk.getZ());
        JsonElement result = fetch.update(vector2, data);
        if(result == null || result.isJsonNull()) return result;

        boolean isCached = redis.set(vector2, data);
        return isCached ? result : null;
    }

    public boolean claim(Chunk chunk, String landId) {
        JsonObject json = new JsonObject();
        json.addProperty("landId", landId);
        JsonElement result = update(chunk, json);
        return result instanceof JsonObject;
    }

    public JsonObject setNonClaimable(Chunk chunk, boolean nonClaimable) {
        return null;
    }


}