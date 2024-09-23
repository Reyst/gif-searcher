package com.github.reyst.giphyapp.domain.repository

import com.github.reyst.giphyapp.domain.entities.GiphySearchResult

interface GiphyRepository {
    suspend fun searchGifs(query: String, offset: Int, amount: Int): Result<GiphySearchResult>
}

