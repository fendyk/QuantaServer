package com.fendyk.DTOs

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

data class ChunkDTO(
        val name: String,
        val x: Int,
        val z: Int
) {
    @JvmField var isClaimable: Boolean? = null
    @JvmField var canExpire: Boolean? = null
    @JvmField var landId: String? = null
    @JvmField var expirationDate: String? = null
    @JvmField var blacklistedBlocks: ArrayList<BlacklistedBlockDTO> = ArrayList()

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
