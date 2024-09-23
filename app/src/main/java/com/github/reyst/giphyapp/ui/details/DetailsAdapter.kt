package com.github.reyst.giphyapp.ui.details

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DetailsAdapter : RecyclerView.Adapter<DetailsVH>() {

    private val items = mutableListOf<DetailOption>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(list: List<DetailOption>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is General -> PROPERTY
            is Image -> IMAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsVH {
        return when (viewType) {
            PROPERTY -> PropertyVH.create(parent)
            IMAGE -> ImageVH.create(parent)
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DetailsVH, position: Int) {
        holder.bind(items[position])
    }

    companion object {
        const val PROPERTY = 1
        const val IMAGE = 2
    }
}