package com.github.reyst.giphyapp.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.github.reyst.giphyapp.R
import com.github.reyst.giphyapp.databinding.GridItemBinding
import com.github.reyst.giphyapp.domain.entities.GiphyGif

class SearchResultAdapter(
    private val onItemClick: (GiphyGif) -> Unit = {},
) : PagingDataAdapter<GiphyGif, GiphyGifVH>(getDiffer()) {

    override fun onBindViewHolder(holder: GiphyGifVH, position: Int) {
        getItem(position)?.also(holder::bind)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiphyGifVH {
        return GiphyGifVH.create(parent)
            .apply {
                itemView.setOnClickListener {
                    getItem(bindingAdapterPosition)?.also(onItemClick)
                }
            }
    }

    companion object {
        private fun getDiffer() = object : DiffUtil.ItemCallback<GiphyGif>() {
            override fun areItemsTheSame(oldItem: GiphyGif, newItem: GiphyGif): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GiphyGif, newItem: GiphyGif): Boolean {
                return oldItem.url == newItem.url
            }
        }
    }
}

class GiphyGifVH private constructor(
    itemView: View,
) : RecyclerView.ViewHolder(itemView) {

    private val binding = GridItemBinding.bind(itemView)

    fun bind(item: GiphyGif) {
        binding.image.load(item.images.original.url)
        binding.image.contentDescription = item.title
    }

    companion object {
        fun create(parent: ViewGroup): GiphyGifVH {
            return LayoutInflater.from(parent.context)
                .inflate(R.layout.grid_item, parent, false)
                .let(::GiphyGifVH)
        }
    }
}