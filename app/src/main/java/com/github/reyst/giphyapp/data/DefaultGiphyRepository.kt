package com.github.reyst.giphyapp.data

import com.github.reyst.giphyapp.domain.entities.GiphyPagingInfo
import com.github.reyst.giphyapp.domain.entities.GiphySearchResult
import com.github.reyst.giphyapp.domain.exceptions.GiphyApiKeyException
import com.github.reyst.giphyapp.domain.exceptions.GiphyBadSearchRequest
import com.github.reyst.giphyapp.domain.exceptions.TooLongQueryException
import com.github.reyst.giphyapp.domain.repository.GiphyRepository
import com.github.reyst.giphyapp.mappers.toDomain
import retrofit2.HttpException

class DefaultGiphyRepository(
    private val dsRemote: RemoteGiphyDataSource,
) : GiphyRepository {
    override suspend fun searchGifs(
        query: String,
        offset: Int,
        amount: Int
    ) = runCatching { dsRemote.search(query, amount, offset).toDomain() }
        .recoverCatching {
            val httpException = it as? HttpException
            when (httpException?.code()) {
                401, 403, 429 -> throw GiphyApiKeyException() // key issues - it's more to developers
                400 -> throw GiphyBadSearchRequest() // request building issue - it's more to developers
                414 -> throw TooLongQueryException()
                404 -> GiphySearchResult(emptyList(), GiphyPagingInfo())
                else -> throw it
            }
        }
}