package com.inrotate.exast.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.inrotate.exast.data.config.ApiConfig
import com.inrotate.exast.presentation.navigation.AppRoute
import com.inrotate.exast.ui.prediction.PredictionTab
import com.inrotate.exast.ui.utils.theme.ExastTheme
import com.inrotate.exast.utils.Prefs
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    val prefs = koinInject<Prefs>()
    val apiConfig = koinInject<ApiConfig>()
    val navBackStack = rememberNavBackStack(
        configuration = appNavSavedStateConfiguration(),
        AppRoute.Ai,
    )
    val selectedRoute = navBackStack.lastOrNull() as? AppRoute ?: AppRoute.Ai
    val initTheme = when (prefs.darkTheme) {
        1 -> true
        0 -> false
        else -> isSystemInDarkTheme()
    }

    var isDarkTheme by remember { mutableStateOf(initTheme) }
    ExastTheme(darkTheme = isDarkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Студенческие мероприятия") },
                    actions = {
                        IconButton(
                            onClick = {
                                isDarkTheme = !isDarkTheme
                                prefs.darkTheme = if (isDarkTheme) 1 else 0
                            },
                        ) {
                            if (isDarkTheme) {
                                Icon(Icons.Rounded.DarkMode, contentDescription = "Темная тема")
                            } else {
                                Icon(Icons.Rounded.LightMode, contentDescription = "Светлая тема")
                            }
                        }
                    },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .safeContentPadding()
                    .fillMaxSize(),
            ) {
                TabRow(selectedTabIndex = AppRoute.entries.indexOf(selectedRoute)) {
                    AppRoute.entries.forEach { route ->
                        Tab(
                            selected = selectedRoute == route,
                            onClick = {
                                if (selectedRoute != route) {
                                    navBackStack.clear()
                                    navBackStack.add(route)
                                }
                            },
                            text = { Text(route.title) },
                        )
                    }
                }
                NavDisplay(
                    backStack = navBackStack,
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                    popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                    predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
                    entryProvider = { route ->
                        NavEntry(route) {
                            if (route == AppRoute.Home) {
                                HomeTab(apiConfig)
                            } else {
                                when (route) {
                                    AppRoute.Home -> PlaceholderTab("Главное")
                                    AppRoute.Feed -> PlaceholderTab("Лента")
                                    AppRoute.Ai -> PredictionTab()
                                    AppRoute.Dashboards -> PlaceholderTab("Дашборды")
                                    else -> PlaceholderTab("Раздел")
                                }
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun HomeTab(apiConfig: ApiConfig) {
    var baseUrl by remember { mutableStateOf(apiConfig.baseUrl) }
    var username by remember { mutableStateOf(apiConfig.username.orEmpty()) }
    var password by remember { mutableStateOf(apiConfig.password.orEmpty()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Настройки API",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        OutlinedTextField(
            value = baseUrl,
            onValueChange = { value ->
                baseUrl = value
                apiConfig.baseUrl = value.trimEnd('/')
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Базовый URL") },
        )
        OutlinedTextField(
            value = username,
            onValueChange = { value ->
                username = value
                apiConfig.username = value.takeIf(String::isNotBlank)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Логин") },
        )
        OutlinedTextField(
            value = password,
            onValueChange = { value ->
                password = value
                apiConfig.password = value.takeIf(String::isNotBlank)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
        )
    }
}

@Composable
private fun appNavSavedStateConfiguration(): SavedStateConfiguration =
    remember {
        SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(AppRoute.serializer())
                }
            }
        }
    }

@Composable
private fun PlaceholderTab(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
