package com.github.reyst.giphyapp.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.reyst.giphyapp.domain.entities.GiphyGif
import com.github.reyst.giphyapp.domain.entities.ImageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties

class DetailsVM : ViewModel() {

    private val _state = MutableStateFlow(DetailsState())
    val state = _state.asStateFlow()

    fun setData(gif: GiphyGif) {
        viewModelScope.launch {

            _state.update { it.copy(url = gif.images.original.url) }

            val generalOptions = async(Dispatchers.Default) {
                gif::class
                    .declaredMemberProperties
                    .filter {
                        it.visibility == KVisibility.PUBLIC
                                && (it.returnType.classifier == String::class ||
                                it.returnType.classifier == Int::class)
                    }
                    .map { General(it.name, it.call(gif).toString()) }
            }

            val images = gif.images
            val imageOptions = async(Dispatchers.Default) {
                images::class
                    .memberProperties
                    .filter {
                        it.visibility == KVisibility.PUBLIC && it.returnType.classifier == ImageData::class
                    }
                    .map { Image(it.name, it.call(images) as ImageData) }
            }

            val options = generalOptions.await() + imageOptions.await()
//            val options = generalOptions + imageOptions
            _state.update { it.copy(options = options) }
        }
    }
}

data class DetailsState(
    val url: String = "",
    val options: List<DetailOption> = emptyList()
)