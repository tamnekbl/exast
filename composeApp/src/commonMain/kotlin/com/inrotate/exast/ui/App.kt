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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.inrotate.exast.getSettings
import com.inrotate.exast.ui.utils.theme.ExastTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import exast.composeapp.generated.resources.Res
import exast.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    var showContent by remember { mutableStateOf(false) }
    val initTheme = when (getSettings().darkTheme) {
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
                        getSettings().darkTheme = if (isDarkTheme) 1 else 0
                    }) {
                        if (isDarkTheme)
                            Icon(Icons.Rounded.DarkMode, contentDescription = "Dark")
                        else
                            Icon(Icons.Rounded.LightMode, contentDescription = "Light")
                    }
                }


                AnimatedVisibility(showContent) {
                    val greeting = remember { Greeting().greet() }
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