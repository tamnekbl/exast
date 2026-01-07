package com.inrotate.exast

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.inrotate.exast.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "exast",
    ) {
        App()
    }
}