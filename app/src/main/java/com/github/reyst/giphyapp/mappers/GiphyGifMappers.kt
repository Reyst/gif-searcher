package com.github.reyst.giphyapp.mappers

import com.github.reyst.giphyapp.data.dto.GiphyGifDto
import com.github.reyst.giphyapp.data.dto.ImageDto
import com.github.reyst.giphyapp.data.dto.ImagesDto
import com.github.reyst.giphyapp.domain.entities.GiphyGif
import com.github.reyst.giphyapp.domain.entities.ImageData
import com.github.reyst.giphyapp.domain.entities.ImageVariants
import com.github.reyst.giphyapp.domain.entities.UNDEFINED_SIZE

fun GiphyGifDto.toDomain() = GiphyGif(
    id.orEmpty(),
    url.orEmpty(),
    url.orEmpty(),
    bitlyUrl.orEmpty(),
    embedUrl.orEmpty(),
    username.orEmpty(),
    source.orEmpty(),
    rating.orEmpty(),
    title.orEmpty(),
    images?.toDomain() ?: ImageVariants(),
)

fun List<GiphyGifDto>.toDomain() = map { it.toDomain() }

fun ImagesDto.toDomain() = ImageVariants(
    original?.toDomain() ?: ImageData(),
    downsizedLarge?.toDomain() ?: ImageData(),
    downsizedMedium?.toDomain() ?: ImageData(),
    downsizedSmall?.toDomain() ?: ImageData(),
    preview?.toDomain() ?: ImageData(),
    previewGif?.toDomain() ?: ImageData(),
    previewWebp?.toDomain() ?: ImageData(),
)

fun ImageDto.toDomain() = ImageData(
    height ?: 0,
    width ?: 0,
    size ?: UNDEFINED_SIZE,
    url.orEmpty(),
    mp4Size ?: UNDEFINED_SIZE,
    mp4Url.orEmpty(),
    webpSize ?: UNDEFINED_SIZE,
    webpUrl.orEmpty(),
)