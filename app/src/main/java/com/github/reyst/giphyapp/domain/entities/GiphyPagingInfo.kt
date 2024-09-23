package com.github.reyst.giphyapp.domain.entities

data class GiphyPagingInfo(
    val offset: Int = 0,
    val total: Int = 0,
    val count: Int = 0,
) {

    val hasPrev: Boolean
        get() = offset > 0

    val hasNext: Boolean
        get() = offset + count < total

}

