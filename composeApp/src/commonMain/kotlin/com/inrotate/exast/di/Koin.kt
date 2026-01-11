package com.inrotate.exast.di

import com.inrotate.exast.ui.Greeting
import com.inrotate.exast.utils.Prefs
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
    single { Prefs(get()) }
    singleOf(::Greeting)
}
