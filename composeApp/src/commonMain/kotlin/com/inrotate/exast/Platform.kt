package com.inrotate.exast

import com.inrotate.exast.utils.SettingsRepository
import com.russhwolf.settings.Settings

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


expect fun getSettings(): SettingsRepository