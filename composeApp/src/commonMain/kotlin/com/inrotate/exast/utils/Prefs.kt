package com.inrotate.exast.utils

import com.russhwolf.settings.Settings

class Prefs (
    override val storage: Settings
): PreferenceHolder {

    var darkTheme by IntPreference(-1)

    fun clear() = storage.clear()
}

