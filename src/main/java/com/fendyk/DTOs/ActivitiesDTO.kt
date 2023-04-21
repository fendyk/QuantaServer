package com.fendyk.DTOs

data class ActivitiesDTO(val id: String) {
    var minecraftUserId: String? = null
    var time: ActivityDTO? = null
    var mining: ArrayList<ActivityDTO> = ArrayList()
    var pvp: ArrayList<ActivityDTO> = ArrayList()
    var pve: ArrayList<ActivityDTO> = ArrayList()
}
