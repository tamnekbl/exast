package com.inrotate.exast

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.inrotate.exast.di.initKoin
import com.inrotate.exast.ui.App
import org.koin.compose.KoinContext

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "exast",
        ) {
            KoinContext {
                App()
            }
        }
    }
}