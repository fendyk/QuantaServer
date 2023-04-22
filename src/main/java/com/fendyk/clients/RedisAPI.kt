package com.fendyk.clients

import com.fendyk.Main
import com.fendyk.utilities.Log
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import io.lettuce.core.pubsub.RedisPubSubListener
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

abstract class RedisAPI<K, DTO>(
        private val redisKey: String,
        private val dtoType: Class<DTO>
) {
    fun fetch(key: String): CompletableFuture<DTO> {
        return CompletableFuture.supplyAsync {
            val inDebugMode = main.serverConfig.isInDebugMode
            val data = syncCommands[redisKey + key]
            if (inDebugMode) {
                Log.info("")
                Log.info("REDIS: getCache is called with key: $key")
                if (data == null || data.length < 1) {
                    Log.warning("Result is null or length is < 1")
                } else {
                    Log.info("Result: " + if (data.length > 250) data.substring(0, 250) + "... (+" + (data.length - 250) + " lines)" else data)
                }
                Log.info("")
            }
            if (data == null) throw Exception("Redis data is null")
            return@supplyAsync Main.gson.fromJson<DTO>(data, dtoType)
        }
    }

    fun save(key: String, data: DTO): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            val inDebugMode = main.serverConfig.isInDebugMode
            val result = syncCommands.set(redisKey + key, Main.gson.toJson(data))
            if (inDebugMode) {
                Log.info("")
                Log.info("REDIS: setCache is called with key: $key")
                Log.info("Result: " + if (result.length > 250) result.substring(0, 250) + "... (+" + (result.length - 250) + " lines)" else result)
                Log.info("")
            }
            return@supplyAsync result == "OK"
        }
    }

    fun find(key: String): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            val inDebugMode = main.serverConfig.isInDebugMode
            val amount = syncCommands.exists(redisKey + key)
            val exists = amount > 0
            if (inDebugMode) {
                Log.info("")
                Log.info("REDIS: existsInCache: $key")
                Log.info("Result: $exists")
                Log.info("")
            }
            return@supplyAsync exists
        }
    }

    open fun get(key: K): CompletableFuture<DTO> {
        throw Exception("GET is not implemented yet.")
    }

    open fun set(key: K, dto: DTO): CompletableFuture<Boolean> {
        throw Exception("GET is not implemented yet.")
    }

    open fun exists(key: K): CompletableFuture<Boolean> {
        throw Exception("GET is not implemented yet.")
    }

    companion object {
        var main: Main = Main.getInstance()
        var client: RedisClient = RedisClient.create(main.serverConfig.redisUrl)
            protected set
        @JvmStatic
        var connection: StatefulRedisConnection<String, String> = client.connect()
            protected set
        protected var pubSubConnection: StatefulRedisPubSubConnection<String, String> = client.connectPubSub()
        protected var syncCommands: RedisCommands<String, String> = connection.sync()
        protected var pubSubCommands: RedisPubSubCommands<String, String>? = null
        fun setListeners(listeners: ArrayList<RedisPubSubListener<String, String>?>) {
            listeners.forEach(Consumer { k: RedisPubSubListener<String, String>? -> pubSubConnection.addListener(k) })
            pubSubCommands = pubSubConnection.sync()
        }

        fun setSubscriptions(subscriptions: ArrayList<String>) {
            subscriptions.forEach(Consumer { k: String -> pubSubCommands!!.subscribe(k) })
        }

        @JvmStatic
        fun connect(url: String?) {
            client = RedisClient.create(main.serverConfig.redisUrl)
            connection = client.connect()
            pubSubConnection = client.connectPubSub()
            syncCommands = connection.sync()
        }
    }
}
