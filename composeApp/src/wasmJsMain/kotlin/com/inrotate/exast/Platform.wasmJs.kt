package com.inrotate.exast

import com.inrotate.exast.utils.Prefs
import com.russhwolf.settings.StorageSettings

class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
}


val  settingsRepository: Prefs by lazy { Prefs(StorageSettings()) }
actual fun getPlatform(): Platform = WasmPlatform()
actual fun getSettings(): Prefs = settingsRepository