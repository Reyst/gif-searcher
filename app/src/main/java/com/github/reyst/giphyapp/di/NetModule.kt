package com.github.reyst.giphyapp.di

import com.github.reyst.giphyapp.R
import com.github.reyst.giphyapp.data.api.ServiceApi
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val netModule = module {

    single { Gson() }

    factory<Converter.Factory> { GsonConverterFactory.create(get()) }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(androidContext().getString(R.string.base_url))
            .addConverterFactory(get())
            .build()
    }

    factory { getRetrofitService(ServiceApi::class.java) }

}

private inline fun <reified T> Scope.getRetrofitService(service: Class<T>): T {
    return get<Retrofit>().create(service)
}
