package com.github.reyst.giphyapp.data.dto

import com.google.gson.annotations.SerializedName

data class GiphySearchResultDto(
    @SerializedName("data") val items: List<GiphyGifDto>?,
    @SerializedName("pagination") val pagination: GiphyPaginationDto?,
    @SerializedName("meta") val meta: GiphyMetaDto?,
)