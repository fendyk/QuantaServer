package com.fendyk.DTOs

import com.google.gson.annotations.SerializedName

data class LandDTO(@SerializedName("id") val id: String) {
    var name: String? = null
    var ownerId: String? = null
    var memberIDs: ArrayList<String> = ArrayList()
    var homes: ArrayList<TaggedLocationDTO> = ArrayList()
    var chunks: ArrayList<ChunkDTO> = ArrayList()
}
