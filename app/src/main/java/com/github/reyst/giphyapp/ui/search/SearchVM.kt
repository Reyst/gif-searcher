package com.github.reyst.giphyapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.reyst.giphyapp.domain.interactors.GiphyGifLoader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest

class SearchVM(
    private val gifLoader: GiphyGifLoader,
) : ViewModel() {

    private val query = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val gifItems = query
        .debounce(500L)
        .flatMapLatest(gifLoader::getGiphyGifPageFlow)
        .cachedIn(viewModelScope)


    fun updateQuery(newQuery: String) {
        query.value = newQuery
    }

}