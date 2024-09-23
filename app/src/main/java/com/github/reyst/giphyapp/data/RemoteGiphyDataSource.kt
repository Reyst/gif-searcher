package com.github.reyst.giphyapp.data

import com.github.reyst.giphyapp.data.api.ServiceApi
import com.github.reyst.giphyapp.data.dto.GiphySearchResultDto


interface RemoteGiphyDataSource {
    suspend fun search(
        query: String,
        amount: Int,
        offset: Int,
    ): GiphySearchResultDto
}

class RetrofitGiphyDataSource(
    private val api: ServiceApi,
    private val apiKey: String,
): RemoteGiphyDataSource {
    override suspend fun search(query: String, amount: Int, offset: Int): GiphySearchResultDto {
        return api.search(apiKey, query, amount, offset)
    }
}