package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.clients.fetch.FetchChunk;
import com.fendyk.clients.redis.RedisChunk;
import com.fendyk.utilities.Vector2;
import com.google.gson.JsonObject;
import org.bukkit.Chunk;

import java.util.UUID;

public class ChunkAPI {

    API api;
    FetchChunk fetch;
    RedisChunk redis;

    public ChunkAPI(API api, FetchChunk fetch, RedisChunk redis) {
        this.api = api;
        this.fetch = fetch;
        this.redis = redis;
    }

    public JsonObject create(Chunk chunk, boolean isNonClaimable) {
        JsonObject data = new JsonObject();
        data.addProperty("isNonClaimable", isNonClaimable);
        data.addProperty("xCoord", chunk.getX());
        data.addProperty("zCoord", chunk.getZ());
        JsonObject result = fetch.create(data);
        boolean isCached = redis.set(new Vector2(chunk.getX(), chunk.getZ()), data);
        return (result != null && isCached) ? result : null;
    }

    public JsonObject update(Chunk chunk, JsonObject data) {
        Vector2 vector2 = new Vector2(chunk.getX(), chunk.getZ();
        JsonObject result = fetch.update(vector2, data);
        boolean isCached = redis.set(vector2, data);
        return (result != null && isCached) ? result : null;
    }

    public boolean claim(Chunk chunk, String landId) {
        JsonObject json = new JsonObject();
        json.addProperty("landId", landId);
        JsonObject result = update(chunk, json);
        return result != null;
    }

    public JsonObject setNonClaimable(Chunk chunk, boolean nonClaimable) {
        return null;
    }


}