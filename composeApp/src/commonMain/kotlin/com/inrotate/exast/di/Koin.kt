package com.inrotate.exast.di

import com.inrotate.exast.data.config.ApiConfig
import com.inrotate.exast.data.network.createHttpClient
import com.inrotate.exast.data.prediction.PredictionApiClient
import com.inrotate.exast.data.prediction.PredictionRepositoryImpl
import com.inrotate.exast.data.prediction.StaticPredictionOptionsRepository
import com.inrotate.exast.domain.prediction.PredictEventScaleUseCase
import com.inrotate.exast.domain.prediction.PredictionOptionsRepository
import com.inrotate.exast.domain.prediction.PredictionRepository
import com.inrotate.exast.presentation.prediction.PredictionPresenter
import com.inrotate.exast.ui.Greeting
import com.inrotate.exast.utils.Prefs
import com.inrotate.exast.utils.logging.AppLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule)
    }
}

expect val platform: Module

val appModule: Module = module {
    includes(platform)
    single { ApiConfig(baseUrl = "http://localhost:8080") }
    singleOf(::AppLogger)
    single { createHttpClient() }
    single { Prefs(get()) }
    singleOf(::Greeting)
    singleOf(::PredictionApiClient)
    single<PredictionRepository> { PredictionRepositoryImpl(get(), get()) }
    single<PredictionOptionsRepository> { StaticPredictionOptionsRepository() }
    singleOf(::PredictEventScaleUseCase)
    singleOf(::PredictionPresenter)
}
