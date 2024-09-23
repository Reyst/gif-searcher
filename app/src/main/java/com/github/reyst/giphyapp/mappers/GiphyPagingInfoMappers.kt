package com.github.reyst.giphyapp.mappers

import com.github.reyst.giphyapp.data.dto.GiphyPaginationDto
import com.github.reyst.giphyapp.domain.entities.GiphyPagingInfo

fun GiphyPaginationDto.toDomain() = GiphyPagingInfo(
    offset = offset ?: 0,
    total = total ?: 0,
    count = count ?: 0,
)