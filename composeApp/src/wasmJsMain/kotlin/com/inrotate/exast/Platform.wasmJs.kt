package com.inrotate.exast

import com.inrotate.exast.utils.SettingsRepository
import com.russhwolf.settings.StorageSettings

class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
}


val  settingsRepository: SettingsRepository by lazy { SettingsRepository(StorageSettings()) }
actual fun getPlatform(): Platform = WasmPlatform()
actual fun getSettings(): SettingsRepository = settingsRepository