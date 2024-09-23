package com.github.reyst.giphyapp.mappers

import com.github.reyst.giphyapp.data.dto.GiphySearchResultDto
import com.github.reyst.giphyapp.domain.entities.GiphyPagingInfo
import com.github.reyst.giphyapp.domain.entities.GiphySearchResult
import com.github.reyst.giphyapp.domain.exceptions.GiphyApiKeyException
import com.github.reyst.giphyapp.domain.exceptions.GiphyBadSearchRequest
import com.github.reyst.giphyapp.domain.exceptions.GiphyDownstreamSystemException
import com.github.reyst.giphyapp.domain.exceptions.TooLongQueryException

fun GiphySearchResultDto.toDomain(): GiphySearchResult {

    return when(meta?.status) {
        401, 403, 429 -> throw GiphyApiKeyException() // key issues - it's more to developers
        400 -> throw GiphyBadSearchRequest() // request building issue - it's more to developers
        414 -> throw TooLongQueryException()
        404 -> GiphySearchResult(emptyList(), GiphyPagingInfo())
        else -> { // 200
            if (meta?.responseId.isNullOrBlank()) throw GiphyDownstreamSystemException() // synthetic response
            else GiphySearchResult(
                items?.toDomain() ?: emptyList(),
                pagination?.toDomain() ?: GiphyPagingInfo(),
            )
        }
    }
}

