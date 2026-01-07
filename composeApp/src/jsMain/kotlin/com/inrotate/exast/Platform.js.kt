package com.inrotate.exast

import com.inrotate.exast.utils.SettingsRepository
import com.russhwolf.settings.StorageSettings

class JsPlatform : Platform {
    override val name: String = "Web with Kotlin/JS"
}
val  settingsRepository: SettingsRepository by lazy { SettingsRepository(StorageSettings()) }

actual fun getPlatform(): Platform = JsPlatform()
actual fun getSettings(): SettingsRepository = settingsRepository