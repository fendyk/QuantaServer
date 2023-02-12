package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.clients.fetch.FetchLand;
import com.fendyk.clients.redis.RedisLand;
import com.google.gson.JsonObject;
import org.bukkit.Chunk;
import java.util.UUID;

public class LandAPI {

    API api;
    FetchLand fetch;
    RedisLand redis;

    public LandAPI(API api, FetchLand fetch, RedisLand redis) {
        this.api = api;
        this.fetch = fetch;
        this.redis = redis;
    }

    public JsonObject create(UUID owner, String name, Chunk chunk) {
        JsonObject json = new JsonObject();
        json.addProperty("owner", owner.toString());
        json.addProperty("name", name);
        json.addProperty("xCoord", chunk.getX());
        json.addProperty("zCoord", chunk.getZ());

        // Create a new chunk
        ChunkAPI chunkAPI = api.getChunkAPI();
        JsonObject Jchunk = chunkAPI.create(chunk, false);

        if (!Jchunk.has("id")) return null;

        // Create new land
        JsonObject result = fetch.create(json);
        boolean isCached = redis.set(owner, json);

        if(result.isJsonNull() || !isCached) return null;

        // Claim the chunk by using the landID
        return chunkAPI.claim(chunk, Jchunk.get("id").getAsString()) ?  Jchunk : null;
    }

}