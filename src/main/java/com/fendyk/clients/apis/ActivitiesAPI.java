package com.fendyk.clients.apis;

import com.fendyk.API;
import com.fendyk.clients.ClientAPI;
import com.fendyk.clients.fetch.FetchActivities;
import com.fendyk.clients.redis.RedisActivities;

public class ActivitiesAPI extends ClientAPI<FetchActivities, RedisActivities> {

    public ActivitiesAPI(API api, FetchActivities fetch, RedisActivities redis) {
        super(api, fetch, redis);
    }

}