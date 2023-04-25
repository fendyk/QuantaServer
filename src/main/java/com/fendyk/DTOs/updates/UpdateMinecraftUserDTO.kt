package com.fendyk.DTOs.updates

import com.fendyk.DTOs.LocationDTO
import com.fendyk.DTOs.SubscriptionRewardDTO
import com.fendyk.DTOs.TaggedLocationDTO

class UpdateMinecraftUserDTO(
        var quanta: Double = 0.0,
        var pushHomes: ArrayList<TaggedLocationDTO> = ArrayList(),
        var spliceHomes: ArrayList<String> = ArrayList(),
        var pushSubscriptionRewards: ArrayList<SubscriptionRewardDTO> = ArrayList(),
        var spliceSubscriptionRewards: ArrayList<String> = ArrayList(),
        var claimSubscriptionRewards: ArrayList<String> = ArrayList(),
        var lastLocation: LocationDTO? = null
)
