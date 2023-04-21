package com.fendyk.DTOs.updates

import com.fendyk.DTOs.ActivityDTO

data class UpdateActivitiesDTO(
        var mining: ArrayList<ActivityDTO> = ArrayList(),
        var pvp: ArrayList<ActivityDTO> = ArrayList(),
        var pve: ArrayList<ActivityDTO> = ArrayList()
)
