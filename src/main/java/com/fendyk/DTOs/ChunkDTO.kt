package com.fendyk.DTOs

import org.joda.time.DateTime

data class ChunkDTO(
        val x: Int,
        val z: Int
) {
    var isClaimable: Boolean? = null
    var canExpire: Boolean? = null
    var landId: String? = null
    var expirationDate: String? = null
    var blacklistedBlocks: ArrayList<BlacklistedBlockDTO> = ArrayList()

    fun setClaimable(claimable: Boolean) {
        isClaimable = claimable
    }

    fun canExpire(): Boolean? {
        return canExpire
    }

    fun getExpirationDate(): DateTime {
        return DateTime(expirationDate)
    }
}
