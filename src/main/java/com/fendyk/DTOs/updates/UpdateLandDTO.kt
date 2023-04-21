package com.fendyk.DTOs.updates

import com.fendyk.DTOs.TaggedLocationDTO

data class UpdateLandDTO(
        var name: String? = null,
        var connectMembers: ArrayList<MemberDTO> = ArrayList(),
        var disconnectMembers: ArrayList<MemberDTO> = ArrayList(),
        var pushHomes: ArrayList<TaggedLocationDTO> = ArrayList(),
        var spliceHomes: ArrayList<String> = ArrayList()
) {
    data class MemberDTO(var id: String)
}
