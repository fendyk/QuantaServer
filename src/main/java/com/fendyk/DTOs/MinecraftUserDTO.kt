package com.fendyk.DTOs

data class MinecraftUserDTO(val id: String) {
    val userId: String? = null
    var authorizeExpireDate: String? = null
    var quanta: Double = 0.0
    var lastLocation: LocationDTO? = null
    var homes: ArrayList<TaggedLocationDTO> = ArrayList()
    var memberLandIDs: ArrayList<String> = ArrayList()
    var subscriptionRewards: ArrayList<SubscriptionRewardDTO> = ArrayList()
}
