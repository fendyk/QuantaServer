package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.redis.RedisBlacklistedChunk;
import com.fendyk.utilities.Vector2;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class BlacklistedChunkAPI extends ClientAPI<Class<?>, RedisBlacklistedChunk> {

    public BlacklistedChunkAPI(API api, Class<?> fetch, RedisBlacklistedChunk redis) {
        super(api, null, redis);
    }

    /**
     * Is the chunk blacklisted?
     * @param chunk
     * @return
     */
    public boolean isBlacklisted(Chunk chunk) {
        return redis.hGet(new Vector2(chunk.getX(), chunk.getZ()));
    }
}
