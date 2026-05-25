package com.inrotate.exast.utils.logging

class AppLogger {
    fun info(tag: String, message: String) {
        println("INFO [$tag] $message")
    }

    fun warn(tag: String, message: String) {
        println("WARN [$tag] $message")
    }

    fun error(tag: String, message: String, throwable: Throwable? = null) {
        val details = throwable?.let { ": ${it::class.simpleName}: ${it.message}" }.orEmpty()
        println("ERROR [$tag] $message$details")
    }
}
