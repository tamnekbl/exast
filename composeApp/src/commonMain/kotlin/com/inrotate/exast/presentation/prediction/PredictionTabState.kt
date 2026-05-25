package com.inrotate.exast.presentation.prediction

import com.inrotate.exast.domain.prediction.OrganizationOption
import com.inrotate.exast.domain.prediction.PredictionOption
import com.inrotate.exast.domain.prediction.PredictionResult

data class PredictionTabState(
    val form: PredictionFormState = PredictionFormState(),
    val levelOptions: List<PredictionOption> = defaultLevelOptions,
    val formatOptions: List<PredictionOption> = defaultFormatOptions,
    val organizationRoleOptions: List<PredictionOption> = defaultOrganizationRoleOptions,
    val typeOptions: List<PredictionOption> = emptyList(),
    val typeQuery: String = "",
    val selectedTypes: List<PredictionOption> = emptyList(),
    val organizationQuery: String = "",
    val organizationsLoading: Boolean = false,
    val organizationSuggestions: List<OrganizationOption> = emptyList(),
    val selectedOrganizations: List<OrganizationOption> = emptyList(),
    val predictionLoading: Boolean = false,
    val predictionResult: PredictionResult? = null,
    val isPredictionStale: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSubmit: Boolean
        get() = form.title.isNotBlank() &&
                form.dateStart.isNotBlank() &&
                form.level.isNotBlank() &&
                form.format.isNotBlank() &&
                form.organizationRole.isNotBlank() &&
                selectedTypes.isNotEmpty() &&
                !predictionLoading
}

data class PredictionFormState(
    val title: String = "",
    val description: String = "",
    val dateStart: String = "",
    val dateEnd: String = "",
    val timeStart: String = "",
    val timeEnd: String = "",
    val level: String = "university",
    val format: String = "offline",
    val organizationRole: String = "organization",
)

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
