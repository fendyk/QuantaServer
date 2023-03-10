package com.fendyk.clients.redis;

import com.fendyk.Main;
import com.fendyk.clients.RedisAPI;
import com.fendyk.utilities.Vector2;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RedisBlacklistedChunk extends RedisAPI<Vector2, Boolean> {

    public RedisBlacklistedChunk(Main server,
                           RedisClient client,
                           boolean inDebugMode,
                           ArrayList<RedisPubSubListener<String, String>> listeners,
                           ArrayList<String> subscriptions) {
        super(server, client, inDebugMode, listeners, subscriptions);
    }

    @Override
    public @Nullable Boolean get(Vector2 key) {
        return null;
    }

    @Override
    public boolean set(Vector2 key, Boolean data) {
        return false;
    }

    @Override
    public boolean exists(Vector2 key) {
        return false;
    }

    public boolean hSet(List<Vector2> keys) {

        // Create a new HashMap with keys and values of type String
        HashMap<String, String> stringHashMap = new HashMap<>();

        // Iterate through the entries of the original HashMap and convert each key to a String
        for (Vector2 location : keys) {
            stringHashMap.put(location.getX() + ":" + location.getY(), "true");
        }

        return syncCommands.hset("blacklistedchunks", stringHashMap) > 0;
    }

    public boolean hGet(Vector2 key) {
        String result = syncCommands.hget("blacklistedchunks", key.getX() + ":" + key.getY());
        return result != null && result.equalsIgnoreCase("true");
    }

    public Long hLen() {
        return syncCommands.hlen("blacklistedchunks");
    }


}
