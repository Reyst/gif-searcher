package com.github.reyst.giphyapp.data

import com.github.reyst.giphyapp.data.api.ServiceApi
import com.github.reyst.giphyapp.data.dto.GiphyMetaDto
import com.github.reyst.giphyapp.data.dto.GiphyPaginationDto
import com.github.reyst.giphyapp.data.dto.GiphySearchResultDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class RetrofitGiphyDataSourceTest {

    private val apiKey = "stub_api_key"
    private val response = GiphySearchResultDto(
        emptyList(),
        GiphyPaginationDto(0, 0, 0),
        GiphyMetaDto("OK", 200, "queryId")
    )

    private val api: ServiceApi = mockk()

    private lateinit var dataSource: RetrofitGiphyDataSource


    @Before
    fun setUp() {
        dataSource = RetrofitGiphyDataSource(api, apiKey)
    }

    @Test
    fun `should add api-key in each query`() = runTest {

        val keySlot = slot<String>()
        coEvery {
            api.search(
                apiKey = capture(keySlot),
                q = any(),
                amount = any(),
                offset = any()
            )
        } returns response

        dataSource.search("test", 10, 0)

        assertEquals(keySlot.captured, apiKey)
        coVerify(exactly = 1) { api.search(apiKey, any(), any(), any()) }

        confirmVerified(api)
    }

    @Test
    fun `data source does not modify response`() = runTest {

        coEvery { api.search(any(), any(), any(), any()) } returns response

        val result = dataSource.search("test", 10, 0)

        assertEquals(response, result)
        coVerify(exactly = 1) { api.search(any(), any(), any(), any()) }

        confirmVerified(api)
    }

    @Test
    fun `data source does not handle exceptions`() = runTest {

        val responseBody = mockk<ResponseBody>(relaxed = true)
        val exception = HttpException(Response.error<GiphySearchResultDto>(400, responseBody))
        coEvery { api.search(any(), any(), any(), any()) } throws exception

        try {
            dataSource.search("test", 10, 0)
            fail("should throw exception")
        } catch (th: Throwable) {
            assertTrue(th is HttpException)
            assertEquals(exception.code(), th.code())
        } finally {
            coVerify(exactly = 1) { api.search(any(), any(), any(), any()) }

            confirmVerified(api)
        }
    }
}