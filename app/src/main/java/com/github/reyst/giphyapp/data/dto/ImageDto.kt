package com.github.reyst.giphyapp.data.dto

import com.google.gson.annotations.SerializedName

data class ImageDto(
    @SerializedName("height") val height: Int?,
    @SerializedName("width") val width: Int?,
    @SerializedName("size") val size: Int?,
    @SerializedName("url") val url: String?,
    @SerializedName("mp4_size") val mp4Size: Int?,
    @SerializedName("mp4") val mp4Url: String?,
    @SerializedName("webp_size") val webpSize: Int?,
    @SerializedName("webp") val webpUrl: String?,
)

