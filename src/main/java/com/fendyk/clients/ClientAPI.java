package com.fendyk.clients;

import com.fendyk.API;

public abstract class ClientAPI<FetchAPI, RedisAPI> {
    protected API api;
    protected FetchAPI fetch;
    protected RedisAPI redis;

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
}
