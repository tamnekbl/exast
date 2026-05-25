package com.inrotate.exast.presentation.prediction

import com.inrotate.exast.domain.prediction.OrganizationOption
import com.inrotate.exast.domain.prediction.PredictionOption
import com.inrotate.exast.domain.prediction.PredictionResult

data class PredictionTabState(
    val title: String = "",
    val description: String = "",
    val dateStart: String = "",
    val dateEnd: String = "",
    val timeStart: String = "",
    val timeEnd: String = "",
    val level: String = "university",
    val format: String = "offline",
    val organizationRole: String = "organization",
    val levelOptions: List<PredictionOption> = defaultLevelOptions,
    val formatOptions: List<PredictionOption> = defaultFormatOptions,
    val organizationRoleOptions: List<PredictionOption> = defaultOrganizationRoleOptions,
    val typeOptions: List<PredictionOption> = emptyList(),
    val organizationOptions: List<OrganizationOption> = emptyList(),
    val selectedTypes: List<String> = emptyList(),
    val selectedOrganizations: List<Int> = emptyList(),
    val typeQuery: String = "",
    val organizationQuery: String = "",
    val isLoading: Boolean = false,
    val result: PredictionResult? = null,
    val errorMessage: String? = null,
) {
    val canSubmit: Boolean
        get() = title.isNotBlank() && dateStart.isNotBlank() && !isLoading
}

val defaultLevelOptions = listOf(
    PredictionOption("structural", "Структурный"),
    PredictionOption("university", "Университетский"),
    PredictionOption("municipal", "Муниципальный"),
    PredictionOption("regional", "Региональный"),
    PredictionOption("interregional", "Межрегиональный"),
    PredictionOption("district", "Окружной"),
    PredictionOption("national", "Национальный"),
    PredictionOption("international", "Международный"),
)

val defaultFormatOptions = listOf(
    PredictionOption("online", "Онлайн"),
    PredictionOption("offline", "Офлайн"),
    PredictionOption("hybrid", "Гибридный"),
)

val defaultOrganizationRoleOptions = listOf(
    PredictionOption("participation", "Участие"),
    PredictionOption("organization", "Организация"),
    PredictionOption("assistance", "Содействие"),
)
