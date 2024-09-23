package com.github.reyst.giphyapp.domain.interactors


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.reyst.giphyapp.domain.entities.GiphyGif
import com.github.reyst.giphyapp.domain.repository.GiphyRepository
import kotlin.math.max

class GiphySearchGifPagingSource(
    private val repository: GiphyRepository,
    private val query: String,
) : PagingSource<Int, GiphyGif>() {

    override fun getRefreshKey(state: PagingState<Int, GiphyGif>): Int? {

        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null

        val pageSize = state.config.pageSize

        val key = (anchorPage.prevKey?.plus(pageSize))
            ?: (anchorPage.nextKey?.minus(pageSize)?.takeIf { it >= 0 })

        return key
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GiphyGif> {

        if (query.isBlank()) {
            return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
        }

        val offset = params.key ?: 0
        val amount = params.loadSize

        val searchResult = repository.searchGifs(query, offset, amount)

        return searchResult.fold(
            onSuccess = {
                val prevKey = if (it.pagination.hasPrev) max(offset - amount, 0) else null
                val nextKey = if (it.pagination.hasNext) offset + amount else null
                LoadResult.Page(it.items, prevKey, nextKey)
            },
            onFailure = {
                LoadResult.Error(it)
            }
        )
    }
}

