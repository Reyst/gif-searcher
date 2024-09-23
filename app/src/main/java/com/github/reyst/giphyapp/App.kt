package com.github.reyst.giphyapp

import android.app.Application
import android.os.Build
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.github.reyst.giphyapp.di.connectedModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initCoil()

        startKoin {
            androidLogger() // Log Koin into Android logger
            androidContext(this@App) // Reference Android context

            // Load modules
            modules(connectedModules)
        }
    }

    private fun initCoil() {
        Coil.setImageLoader {
            val decoderFactory =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ImageDecoderDecoder.Factory()
                else GifDecoder.Factory()

            ImageLoader
                .Builder(this@App)
                .components { add(decoderFactory) }
                .build()
        }
    }
}