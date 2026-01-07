package com.inrotate.exast

import com.inrotate.exast.utils.SettingsRepository
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()


val settingsRepository: SettingsRepository by lazy {
    val preferences = Preferences.userRoot()
    val settings = PreferencesSettings(preferences)
    SettingsRepository(settings)
}
actual fun getSettings(): SettingsRepository = settingsRepository