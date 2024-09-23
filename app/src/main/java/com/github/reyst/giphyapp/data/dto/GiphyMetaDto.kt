package com.github.reyst.giphyapp.data.dto

import com.google.gson.annotations.SerializedName

data class GiphyMetaDto(
    @SerializedName("msg") val msg: String,
    @SerializedName("status") val status: Int,
    @SerializedName("response_id") val responseId: String?,
)