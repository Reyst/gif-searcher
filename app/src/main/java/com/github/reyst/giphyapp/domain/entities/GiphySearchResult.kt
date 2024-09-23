package com.github.reyst.giphyapp.domain.entities

data class GiphySearchResult(
    val items: List<GiphyGif>,
    val pagination: GiphyPagingInfo,
)