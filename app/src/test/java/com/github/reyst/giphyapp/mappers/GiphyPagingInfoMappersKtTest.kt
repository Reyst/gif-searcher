package com.github.reyst.giphyapp.mappers

import com.github.reyst.giphyapp.data.dto.GiphyPaginationDto
import org.junit.Test
import kotlin.test.assertEquals

class GiphyPagingInfoMappersKtTest {

    @Test
    fun `null should become 0`() {
        val result = GiphyPaginationDto(null, null, null).toDomain()
        assertEquals(0, result.total)
        assertEquals(0, result.offset)
        assertEquals(0, result.count)
    }

    @Test
    fun `non-null values should be transferred without changes`() {
        val result = GiphyPaginationDto(offset = 1, total = 3, count = 2).toDomain()
        assertEquals(3, result.total)
        assertEquals(1, result.offset)
        assertEquals(2, result.count)
    }
}