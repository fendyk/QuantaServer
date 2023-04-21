package com.fendyk.DTOs

import com.google.gson.annotations.SerializedName
import org.bukkit.Bukkit
import org.bukkit.Location

data class LocationDTO(
        var x: Double,
        var y: Double,
        var z: Double,
        var yaw: Float,
        var pitch: Float,
        val world: String
) {

    constructor(location: Location): this(
            location.x,
            location.y,
            location.z,
            location.yaw,
            location.pitch,
            location.world.name
    )

    companion object {
        @JvmStatic
        fun toLocation(locationDTO: LocationDTO): Location {
            return Location(
                    Bukkit.getWorld(locationDTO.world),
                    locationDTO.x,
                    locationDTO.y,
                    locationDTO.z,
                    locationDTO.yaw,
                    locationDTO.pitch
            )
        }
    }
}
