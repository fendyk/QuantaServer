package com.fendyk.clients

abstract class ClientAPI<FetchAPI, RedisAPI, Key, DTO>(@JvmField var fetch: FetchAPI, @JvmField var redis: RedisAPI) {

    @JvmField
    protected var cachedRecords = HashMap<Key, DTO>()

    /**
     * Returns the cached player, pure for UI/Visuals that require loads of updates only.
     * @param key
     * @return DTO
     */
    fun getCached(key: Key): DTO? {
        return cachedRecords[key]
    }
}
