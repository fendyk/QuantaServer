package com.fendyk.DTOs.updates

import com.fendyk.DTOs.BlacklistedBlockDTO

data class UpdateChunkDTO(
        var isClaimable: Boolean? = null,
        var canExpire: Boolean? = null,
        var landId: String? = null,
        var expirationDate: String? = null,
        var resetExpirationDate: Boolean? = null,
        var resetLandId: Boolean? = null,
        var pushBlacklistedBlocks: ArrayList<BlacklistedBlockDTO> = ArrayList(),
        var spliceBlacklistedBlocks: ArrayList<BlacklistedBlockDTO> = ArrayList()
)
