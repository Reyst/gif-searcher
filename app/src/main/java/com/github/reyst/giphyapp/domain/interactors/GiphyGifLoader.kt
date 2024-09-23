package com.github.reyst.giphyapp.domain.interactors

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.github.reyst.giphyapp.domain.repository.GiphyRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class GiphyGifLoader(
    private val repository: GiphyRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    fun getGiphyGifPageFlow(query: String) = Pager(
        config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { GiphySearchGifPagingSource(repository, query) }
    )
        .flow
        .flowOn(dispatcher)

    companion object {
        const val NETWORK_PAGE_SIZE = 20
    }
}