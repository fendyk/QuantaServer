package com.fendyk.DTOs

data class SubscriptionRewardDTO(
        var isClaimed: Boolean,
        var quanta: Double,
        var crateKeys: Int,
        var createdAt: String
)
