package com.inrotate.exast.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
enum class AppRoute(
    val title: String,
) : NavKey {
    Home("Главное"),
    Feed("Лента"),
    Ai("AI"),
    Dashboards("Дашборды"),
}
