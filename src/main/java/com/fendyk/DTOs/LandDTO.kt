package com.fendyk.DTOs

data class LandDTO(
        val id: String = "",
        var name: String? = null,
        var ownerId: String? = null,
        var memberIDs: ArrayList<String> = ArrayList(),
        var homes: ArrayList<TaggedLocationDTO> = ArrayList(),
        var chunks: ArrayList<ChunkDTO> = ArrayList()
)
