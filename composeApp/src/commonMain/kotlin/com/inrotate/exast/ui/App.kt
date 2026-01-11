package com.inrotate.exast.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.inrotate.exast.ui.utils.theme.ExastTheme
import com.inrotate.exast.utils.Prefs
import exast.composeapp.generated.resources.Res
import exast.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    var showContent by remember { mutableStateOf(false) }
    val prefs = koinInject<Prefs>()
    val model = koinInject<Greeting>()
    val initTheme = when (prefs.darkTheme) {
        1 -> true
        0 -> false
        else -> isSystemInDarkTheme()
    }

    var isDarkTheme by remember { mutableStateOf(initTheme) }
    ExastTheme(darkTheme = isDarkTheme) {
        Scaffold {
            Column(
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = { showContent = !showContent }) {
                        Text("Click me!")
                    }
                    IconButton(onClick = {
                        isDarkTheme = !isDarkTheme
                        prefs.darkTheme = if (isDarkTheme) 1 else 0
                    }) {
                        if (isDarkTheme)
                            Icon(Icons.Rounded.DarkMode, contentDescription = "Dark")
                        else
                            Icon(Icons.Rounded.LightMode, contentDescription = "Light")
                    }
                }


                AnimatedVisibility(showContent) {
                    val greeting = remember { model.greet() }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(painterResource(Res.drawable.compose_multiplatform), null)
                        Text("Compose: $greeting")
                    }
                }
            }
        }
    }
}