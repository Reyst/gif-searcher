package com.github.reyst.giphyapp.ui.details

import com.github.reyst.giphyapp.domain.entities.ImageData

sealed interface DetailOption
data class General(
    val title: String,
    val value: String,
) : DetailOption

data class Image(
    val title: String,
    val value: ImageData,
): DetailOption