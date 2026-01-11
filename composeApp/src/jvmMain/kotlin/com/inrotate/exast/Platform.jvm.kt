package com.inrotate.exast

import com.inrotate.exast.utils.Prefs
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()


val prefs: Prefs by lazy {
    val preferences = Preferences.userRoot()
    val settings = PreferencesSettings(preferences)
    Prefs(settings)
}
actual fun getSettings(): Prefs = prefs