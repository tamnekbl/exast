package com.inrotate.exast.ui

import com.inrotate.exast.getPlatform

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}