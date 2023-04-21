package com.fendyk.clients;

import com.fendyk.API;

import java.util.HashMap;

public abstract class ClientAPI<FetchAPI, RedisAPI, Key, DTO> {
    protected API api;
    protected FetchAPI fetch;
    protected RedisAPI redis;

    protected HashMap<Key, DTO> cachedRecords = new HashMap<>();

    public ClientAPI(API api, FetchAPI fetch, RedisAPI redis) {
        this.api = api;
        this.fetch = fetch;
        this.redis = redis;
    }
    public FetchAPI getFetch() {
        return fetch;
    }

    public RedisAPI getRedis() {
        return redis;
    }


    /**
     * Returns the cached player, pure for UI/Visuals that require loads of updates only.
     * @param key
     * @return DTO
     */
    public DTO getCached(Key key) {
        return cachedRecords.get(key);
    }

}
