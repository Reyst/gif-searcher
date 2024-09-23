package com.github.reyst.giphyapp.di

import com.github.reyst.giphyapp.R
import com.github.reyst.giphyapp.data.DefaultGiphyRepository
import com.github.reyst.giphyapp.data.RemoteGiphyDataSource
import com.github.reyst.giphyapp.data.RetrofitGiphyDataSource
import com.github.reyst.giphyapp.domain.interactors.GiphyGifLoader
import com.github.reyst.giphyapp.domain.repository.GiphyRepository
import com.github.reyst.giphyapp.ui.search.SearchVM
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val searchModule = module {

    factory<RemoteGiphyDataSource> {
        RetrofitGiphyDataSource(
            get(),
            androidContext().getString(R.string.giphy_key)
        )
    }

    single<GiphyRepository> { DefaultGiphyRepository(get()) }

    factory { GiphyGifLoader(get()) }

    viewModelOf(::SearchVM)
}

