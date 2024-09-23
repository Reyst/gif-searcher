package com.github.reyst.giphyapp

import android.content.Context
import com.github.reyst.giphyapp.di.connectedModules
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules


class KoinModulesTest : KoinTest {

    @Test
    fun verifyKoinApp() {
        val ctx = mockk<Context>()

        every { ctx.getString(any(Int::class)) } answers { it ->
            if(it.invocation.args[0] == R.string.base_url) "https://base.api.url/"
            else "test string"
        }

        koinApplication {
            androidContext(ctx)
            modules(connectedModules)
            checkModules()
        }
    }
}

