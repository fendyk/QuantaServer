package com.fendyk.DTOs

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

data class SubscriptionRewardDTO(
        var isClaimed: Boolean,
        var quanta: Double,
        var crateKeys: Int,
        var createdAt: String
)
