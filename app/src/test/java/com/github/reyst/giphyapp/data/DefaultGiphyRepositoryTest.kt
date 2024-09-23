package com.github.reyst.giphyapp.data

import com.github.reyst.giphyapp.data.dto.GiphyMetaDto
import com.github.reyst.giphyapp.data.dto.GiphySearchResultDto
import com.github.reyst.giphyapp.domain.exceptions.GiphyApiKeyException
import com.github.reyst.giphyapp.domain.exceptions.GiphyBadSearchRequest
import com.github.reyst.giphyapp.domain.exceptions.GiphyDownstreamSystemException
import com.github.reyst.giphyapp.domain.exceptions.TooLongQueryException
import com.github.reyst.giphyapp.domain.repository.GiphyRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultGiphyRepositoryTest {

    private val ds: RemoteGiphyDataSource = mockk()
    private lateinit var repository: GiphyRepository

    @Before
    fun setUp() {
        repository = DefaultGiphyRepository(ds)
    }

    @Test
    fun `Result should contains GiphyApiKeyException (401, 403, 429)`() = runTest {

        val responseBody = mockk<ResponseBody>(relaxed = true)
        val exceptions = listOf(401, 403, 429)
            .map { HttpException(Response.error<GiphySearchResultDto>(it, responseBody)) }

        coEvery { ds.search(any(), any(), any()) } throwsMany exceptions

        val results = mutableListOf(
            repository.searchGifs("test", 0, 20),
            repository.searchGifs("test", 0, 20),
            repository.searchGifs("test", 0, 20),
        )

        assertEquals(3, results.size)
        results
            .mapNotNull { it.exceptionOrNull() }
            .filterIsInstance<GiphyApiKeyException>()
            .also { assertEquals(3, it.size) }

        coVerify(exactly = 3) { ds.search(any(), any(), any()) }

        confirmVerified(ds)
    }

    @Test
    fun `Should return empty result if 404`() = runTest {

        val responseBody = mockk<ResponseBody>(relaxed = true)
        val exception = HttpException(Response.error<GiphySearchResultDto>(404, responseBody))

        coEvery { ds.search(any(), any(), any()) } throws exception

        val result = repository.searchGifs("test", 0, 20)
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.items?.isEmpty() ?: false)

        coVerify(exactly = 1) { ds.search(any(), any(), any()) }
        confirmVerified(ds)
    }


    @Test
    fun `Result should contains GiphyBadSearchRequest (400)`() = runTest {
        val responseBody = mockk<ResponseBody>(relaxed = true)
        val exception = HttpException(Response.error<GiphySearchResultDto>(400, responseBody))

        coEvery { ds.search(any(), any(), any()) } throws exception

        val result = repository.searchGifs("test", 0, 20)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.let { it is GiphyBadSearchRequest } ?: false)

        coVerify(exactly = 1) { ds.search(any(), any(), any()) }
        confirmVerified(ds)
    }

    @Test
    fun `Result should contains TooLongQueryException (414)`() = runTest {
        val responseBody = mockk<ResponseBody>(relaxed = true)
        val exception = HttpException(Response.error<GiphySearchResultDto>(414, responseBody))

        coEvery { ds.search(any(), any(), any()) } throws exception

        val result = repository.searchGifs("test", 0, 20)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.let { it is TooLongQueryException } ?: false)

        coVerify(exactly = 1) { ds.search(any(), any(), any()) }
        confirmVerified(ds)
    }

    @Test
    fun `Result should contains GiphyDownstreamSystemException`() = runTest {

        coEvery { ds.search(any(), any(), any()) } returns GiphySearchResultDto(emptyList(), null, GiphyMetaDto("OK", 200, ""))

        val result = repository.searchGifs("test", 0, 20)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.let { it is GiphyDownstreamSystemException } ?: false)

        coVerify(exactly = 1) { ds.search(any(), any(), any()) }
        confirmVerified(ds)
    }
}