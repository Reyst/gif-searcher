package com.github.reyst.giphyapp.data.dto

import com.google.gson.annotations.SerializedName

data class GiphyPaginationDto(
    @SerializedName("offset") val offset: Int?,
    @SerializedName("total_count") val total: Int?,
    @SerializedName("count") val count: Int?,
)