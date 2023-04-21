package com.fendyk.DTOs

import com.google.gson.annotations.SerializedName
import java.util.*

data class MinecraftUserDTO(val id: String) {
    val userId: String? = null
    var authorizeExpireDate: String? = null
    var quanta: Double? = null
    var lastLocation: LocationDTO? = null
    var homes: ArrayList<TaggedLocationDTO> = ArrayList()
    var memberLandIDs: ArrayList<String> = ArrayList()
    var subscriptionRewards: ArrayList<SubscriptionRewardDTO> = ArrayList()
}
