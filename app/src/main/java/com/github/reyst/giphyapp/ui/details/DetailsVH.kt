package com.github.reyst.giphyapp.ui.details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.reyst.giphyapp.R
import com.github.reyst.giphyapp.databinding.ImageDetailBinding
import com.github.reyst.giphyapp.databinding.PropertyDetailBinding

abstract class DetailsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: DetailOption)
}

class PropertyVH private constructor(itemView: View) : DetailsVH(itemView) {

    private val binding = PropertyDetailBinding.bind(itemView)

    override fun bind(item: DetailOption) {
        val castedItem = item as? General
        binding.title.text = castedItem?.title.orEmpty()
        binding.value.text = castedItem?.value.orEmpty()
    }

    companion object {
        fun create(parent: ViewGroup): PropertyVH {
            return LayoutInflater.from(parent.context)
                .inflate(R.layout.property_detail, parent, false)
                .let(::PropertyVH)
        }
    }
}

class ImageVH private constructor(itemView: View) : DetailsVH(itemView) {

    private val binding = ImageDetailBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    override fun bind(item: DetailOption) {

        val castedItem = item as? Image
        binding.title.text = castedItem?.title.orEmpty()

        val imageData = castedItem?.value

        binding.dimension.text = "${imageData?.width ?: 0} x ${imageData?.height ?: 0}"
        binding.size.text = getSizeString("size", imageData?.size ?: 0)
        binding.url.text = getUrlString("url", imageData?.url.orEmpty())
        binding.mp4Size.text = getSizeString("MP4 size", imageData?.mp4Size ?: 0)
        binding.mp4Url.text = getUrlString("MP4 url", imageData?.mp4Url.orEmpty())
        binding.webpSize.text = getSizeString("Webp size", imageData?.webpSize ?: 0)
        binding.webpUrl.text = getUrlString("Webp url", imageData?.webpUrl.orEmpty())
    }

    private fun getSizeString(title: String, size: Int): String {
        val sizeStr = size.takeIf { it > 0 }
            ?.toString()
            ?: " - "

        return "$title: $sizeStr"
    }

    private fun getUrlString(title: String, url: String) = "$title: ${url.ifBlank { " - " }}"

    companion object {
        fun create(parent: ViewGroup): ImageVH {
            return LayoutInflater.from(parent.context)
                .inflate(R.layout.image_detail, parent, false)
                .let(::ImageVH)
        }
    }
}