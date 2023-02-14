package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.clients.fetch.FetchLand;
import com.fendyk.clients.redis.RedisLand;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.leonhard.storage.Json;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.util.Arrays;
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

    /**
     * Creates a new land. Requires an existing chunk
     * @param owner
     * @param name
     * @param chunk
     * @return The new land if created or null if something went wrong.
     */
    public JsonObject create(UUID owner, String name, Chunk chunk) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("name", name);

            /* Get references */
            ChunkAPI chunkAPI = api.getChunkAPI();
            MinecraftUserAPI minecraftUserAPI = api.getMinecraftUserAPI();

            /* Get user */
            JsonElement eUser = minecraftUserAPI.get(owner);
            if(!(eUser instanceof JsonObject)) throw new Exception("Could not find user when creating land");

            JsonObject jUser = eUser.getAsJsonObject(); // Get the jsonobject
            json.addProperty("userId", jUser.get("id").getAsString()); // Add user id property

            /* Get chunk */
            JsonElement eChunk = chunkAPI.get(chunk);

            /* If we cannot find one */
            if(!(eChunk instanceof JsonObject)) {
                Bukkit.getLogger().info("I need a chunk!");
                eChunk = chunkAPI.create(chunk, false);
                if(!(eChunk instanceof JsonObject)) throw new Exception("Could not create chunk when creating land");
            }

            JsonObject jChunk = eChunk.getAsJsonObject();

            Bukkit.getLogger().info("I found a chunk!");
            Bukkit.getLogger().info(jChunk.toString());

            json.addProperty("chunkId", jChunk.get("id").getAsString()); // add chunkId

            /* Create the actual land */
            JsonElement eLand = fetch.create(json);
            if(!(eLand instanceof JsonObject)) throw new Exception("Could not create land");
            JsonObject jLand = eLand.getAsJsonObject();

            /* Cache it into redis */
            boolean isCached = redis.set(owner, jLand); // Put the eLand into the cache
            if(!isCached) throw new Exception("Could not cache land when creating.");

            Bukkit.getLogger().info(" I created a land");
            Bukkit.getLogger().info(jLand.toString());

            boolean isClaimed = chunkAPI.claim(chunk, jChunk.get("id").getAsString()); // Claim chunk
            if(!isClaimed) throw new Exception("Could not claim chunk when creating land");

            return jLand;
        } catch(Exception e) {
            Bukkit.getLogger().warning(e.toString());
            Bukkit.getLogger().warning(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    public JsonElement get(UUID owner) {
        return ObjectUtils.firstNonNull(redis.get(owner), fetch.get(owner));
    }

}