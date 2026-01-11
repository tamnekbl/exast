package com.inrotate.exast

import com.inrotate.exast.utils.Prefs
import com.russhwolf.settings.StorageSettings

class JsPlatform : Platform {
    override val name: String = "Web with Kotlin/JS"
}
val  settingsRepository: Prefs by lazy { Prefs(StorageSettings()) }

actual fun getPlatform(): Platform = JsPlatform()
actual fun getSettings(): Prefs = settingsRepository