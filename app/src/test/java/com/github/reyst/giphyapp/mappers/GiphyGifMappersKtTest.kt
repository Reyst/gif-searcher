package com.github.reyst.giphyapp.mappers

import com.github.reyst.giphyapp.data.dto.ImageDto
import com.github.reyst.giphyapp.data.dto.ImagesDto
import org.junit.Test
import kotlin.test.assertEquals

class GiphyGifMappersKtTest {

    @Test
    fun `IMAGE non-null values should be transferred without changes`() {
        val result = ImageDto(
            height = 100,
            width = 200,
            size = 1,
            url = "url",
            mp4Size = 2,
            mp4Url = "mp4",
            webpSize = 3,
            webpUrl = "webp",
        ).toDomain()

        assertEquals(1, result.size)
        assertEquals(2, result.mp4Size)
        assertEquals(3, result.webpSize)

        assertEquals(100, result.height)
        assertEquals(200, result.width)

        assertEquals("url", result.url)
        assertEquals("mp4", result.mp4Url)
        assertEquals("webp", result.webpUrl)
    }


    @Test
    fun `ImagesDto source fields should match destination fields`() {
        val result = ImagesDto(
            original = createImageWithUrl("original"),
            downsizedLarge = createImageWithUrl("downsizedLarge"),
            downsizedMedium = createImageWithUrl("downsizedMedium"),
            downsizedSmall = createImageWithUrl("downsizedSmall"),
            preview = createImageWithUrl("preview"),
            previewGif = createImageWithUrl("previewGif"),
            previewWebp = createImageWithUrl("previewWebp"),
        ).toDomain()

        assertEquals("original", result.original.url)
        assertEquals("downsizedLarge", result.downsizedLarge.url)
        assertEquals("downsizedMedium", result.downsizedMedium.url)
        assertEquals("downsizedSmall", result.downsizedSmall.url)
        assertEquals("preview", result.preview.url)
        assertEquals("previewGif", result.previewGif.url)
        assertEquals("previewWebp", result.previewWebp.url)
    }

    private fun createImageWithUrl(url: String) = ImageDto(
        height = null,
        width = null,
        size = null,
        url = url,
        mp4Size = null,
        mp4Url = null,
        webpSize = null,
        webpUrl = null,
    )
}