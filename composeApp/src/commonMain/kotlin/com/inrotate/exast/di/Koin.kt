package com.inrotate.exast.di

import com.inrotate.exast.data.config.ApiConfig
import com.inrotate.exast.data.network.createHttpClient
import com.inrotate.exast.data.prediction.OrganizationsApiClient
import com.inrotate.exast.data.prediction.PredictionApiClient
import com.inrotate.exast.data.prediction.PredictionOptionsRepositoryImpl
import com.inrotate.exast.data.prediction.PredictionRepositoryImpl
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
    single {
        ApiConfig(
            baseUrl = "http://localhost:8080/api/v1",
            username = "admin",
            password = "1",
        )
    }
    singleOf(::AppLogger)
    single { createHttpClient(get()) }
    single { Prefs(get()) }
    singleOf(::Greeting)
    singleOf(::PredictionApiClient)
    singleOf(::OrganizationsApiClient)
    single<PredictionRepository> { PredictionRepositoryImpl(get(), get()) }
    single<PredictionOptionsRepository> { PredictionOptionsRepositoryImpl(get(), get()) }
    singleOf(::PredictEventScaleUseCase)
    singleOf(::PredictionPresenter)
}
