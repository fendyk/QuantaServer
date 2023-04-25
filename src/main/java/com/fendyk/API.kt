package com.fendyk

import com.fendyk.clients.FetchAPI.Companion.connect
import com.fendyk.clients.RedisAPI
import com.fendyk.clients.RedisAPI.Companion.connect
import com.fendyk.clients.RedisAPI.Companion.connection
import com.fendyk.clients.apis.ActivitiesAPI
import com.fendyk.clients.apis.ChunkAPI
import com.fendyk.clients.apis.LandAPI
import com.fendyk.clients.apis.MinecraftUserAPI
import com.fendyk.clients.fetch.FetchActivities
import com.fendyk.clients.fetch.FetchChunk
import com.fendyk.clients.fetch.FetchLand
import com.fendyk.clients.fetch.FetchMinecraftUser
import com.fendyk.clients.redis.RedisActivities
import com.fendyk.clients.redis.RedisChunk
import com.fendyk.clients.redis.RedisLand
import com.fendyk.clients.redis.RedisMinecraftUser
import com.fendyk.listeners.AuthenticationListener
import com.fendyk.listeners.ChunkListener
import com.fendyk.listeners.LandListener
import com.fendyk.utilities.Log
import io.lettuce.core.pubsub.RedisPubSubListener

class API(server: Main?) {
    var main = Main.instance

    @JvmField
    var activitiesAPI: ActivitiesAPI

    @JvmField
    var minecraftUserAPI: MinecraftUserAPI

    @JvmField
    var landAPI: LandAPI

    @JvmField
    var chunkAPI: ChunkAPI

    init {
        val serverConfig = main.serverConfig
        val redisUrl = serverConfig.redisUrl
        val apiUrl = serverConfig.apiUrl
        val jwtToken = serverConfig.jwtToken
        val listeners = ArrayList<RedisPubSubListener<String, String>?>()
        listeners.add(AuthenticationListener(server))
        listeners.add(ChunkListener(server))
        listeners.add(LandListener(server))
        val subscriptions = ArrayList<String>()
        subscriptions.add("authentication")
        subscriptions.add("chunk")
        subscriptions.add("land")

        // Make the connection to redis
        connect(redisUrl)
        RedisAPI.setListeners(listeners)
        RedisAPI.setSubscriptions(subscriptions)

        // Connect to the fetchAPI
        connect(apiUrl, jwtToken)
        if (!connection.isOpen) {
            Log.error("Could not connect to redis, is the server offline?")
        }
        Log.success("Connection to the redis server has been successful!")

        activitiesAPI = ActivitiesAPI(
                FetchActivities(),
                RedisActivities()
        )
        minecraftUserAPI = MinecraftUserAPI(
                FetchMinecraftUser(),
                RedisMinecraftUser()
        )
        landAPI = LandAPI(
                FetchLand(),
                RedisLand()
        )
        chunkAPI = ChunkAPI(
                FetchChunk(),
                RedisChunk()
        )
    }
}
