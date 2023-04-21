package com.fendyk.DTOs

import com.google.gson.annotations.SerializedName
import org.bukkit.Location

data class TaggedLocationDTO(
        var name: String,
        var location: Location
)