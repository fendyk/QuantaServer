package com.fendyk.DTOs

import java.util.*

data class MinecraftUserDTO(val id: String) {

    @JvmField
    var userId: String? = null

    var authorizeExpireDate: Date? = null

    @JvmField
    var quanta: Double? = null

    var homes: ArrayList<TaggedLocationDTO> = ArrayList()
    var memberLandIDs: ArrayList<String> = ArrayList()

    @JvmField
    var subscriptionRewards: ArrayList<SubscriptionRewardDTO> = ArrayList()

    @JvmField
    var lastLocation: LocationDTO? = null

}
