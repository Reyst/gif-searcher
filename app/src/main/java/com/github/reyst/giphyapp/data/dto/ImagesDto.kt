package com.github.reyst.giphyapp.data.dto

import com.google.gson.annotations.SerializedName

data class ImagesDto(
    @SerializedName("original") val original: ImageDto?,
    @SerializedName("downsized_large") val downsizedLarge: ImageDto?,
    @SerializedName("downsized_medium") val downsizedMedium: ImageDto?,
    @SerializedName("downsized_small") val downsizedSmall: ImageDto?,
    @SerializedName("preview") val preview: ImageDto?,
    @SerializedName("preview_gif") val previewGif: ImageDto?,
    @SerializedName("preview_webp") val previewWebp: ImageDto?,
)