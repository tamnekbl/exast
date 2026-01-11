package com.inrotate.exast.ui

import com.inrotate.exast.utils.Platform

class Greeting(
    private val platform: Platform
) {

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}