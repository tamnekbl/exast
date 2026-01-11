package com.inrotate.exast.di

import com.inrotate.exast.JsPlatform
import com.inrotate.exast.utils.Platform
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import org.koin.dsl.module

actual val platform = module {
    single<Settings> { StorageSettings() }
    single<Platform> { JsPlatform() }
}
