package com.github.reyst.giphyapp.data.api

import com.github.reyst.giphyapp.data.dto.GiphySearchResultDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ServiceApi {

    @GET("v1/gifs/search")
    suspend fun search(
        // Maybe it'd be good idea move adding of the API_KEY into an interceptor,
        // but for 1/2 methods it is not important
        @Query("api_key") apiKey: String,
        @Query("q") q: String,
        @Query("limit") amount: Int, // default 25, max for beta 50
        @Query("offset") offset: Int, // default 0, max 4999
    ): GiphySearchResultDto
}

