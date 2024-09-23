package com.github.reyst.giphyapp.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class GiphyGif(
    val id: String,
    val url: String,
    val slug: String = "",
    val bitlyUrl: String = "",
    val embedUrl: String = "",
    val username: String = "",
    val source: String = "",
    val rating: String = "",
    val title: String = "",
    val images: ImageVariants = ImageVariants(),
): Parcelable

@Parcelize
data class ImageVariants(
    val original: ImageData = ImageData(),
    val downsizedLarge: ImageData = ImageData(),
    val downsizedMedium: ImageData = ImageData(),
    val downsizedSmall: ImageData = ImageData(),
    val preview: ImageData = ImageData(),
    val previewGif: ImageData = ImageData(),
    val previewWebp: ImageData = ImageData(),
): Parcelable

@Parcelize
data class ImageData(
    val height: Int = 0,
    val width: Int = 0,
    val size: Int = UNDEFINED_SIZE,
    val url: String = "",
    val mp4Size: Int = UNDEFINED_SIZE,
    val mp4Url: String = "",
    val webpSize: Int = UNDEFINED_SIZE,
    val webpUrl: String = "",
): Parcelable

const val UNDEFINED_SIZE = -1
