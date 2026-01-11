package com.inrotate.exast.di

import com.inrotate.exast.JVMPlatform
import com.inrotate.exast.utils.Platform
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.koin.dsl.module
import java.util.prefs.Preferences

actual val platform = module {
    single<Settings> { PreferencesSettings(Preferences.userRoot()) }
    single<Platform> { JVMPlatform() }
}
