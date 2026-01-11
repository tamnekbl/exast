package com.inrotate.exast

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.inrotate.exast.di.initKoin
import com.inrotate.exast.ui.App
import org.koin.compose.KoinContext

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()
    ComposeViewport {
        KoinContext {
            App()
        }
    }
}