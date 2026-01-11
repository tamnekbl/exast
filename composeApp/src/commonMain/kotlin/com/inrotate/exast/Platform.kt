package com.inrotate.exast

import com.inrotate.exast.utils.Prefs

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


expect fun getSettings(): Prefs