package com.github.reyst.giphyapp.data.dto

import com.google.gson.annotations.SerializedName

data class GiphyGifDto(
    @SerializedName("type") val type: String?,
    @SerializedName("id") val id: String?,
    @SerializedName("slug") val slug: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("bitly_url") val bitlyUrl: String?,
    @SerializedName("embed_url") val embedUrl: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("source") val source: String?,
    @SerializedName("images") val images: ImagesDto?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("title") val title: String?,
)