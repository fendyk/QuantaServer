package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.DTOs.ActivityDTO;
import com.fendyk.DTOs.ChunkDTO;
import com.fendyk.DTOs.updates.UpdateChunkDTO;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.fetch.FetchChunk;
import com.fendyk.clients.redis.RedisChunk;
import com.fendyk.utilities.Vector2;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.Chunk;
import org.jetbrains.annotations.Nullable;

public class ActivityAPI extends ClientAPI<FetchChunk, Class<?>> {

    public ActivityAPI(API api, FetchChunk fetch, Class<?> redis) {
        super(api, fetch, null);
    }

    public void update(ActivityDTO activityDTO) {

    }

}