package com.github.reyst.giphyapp.di

import com.github.reyst.giphyapp.ui.details.DetailsVM
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val detailsModule = module {
    viewModelOf(::DetailsVM)
}