package com.inrotate.exast.presentation.prediction

sealed interface PredictionTabEvent {
    data class TitleChanged(val value: String) : PredictionTabEvent
    data class DescriptionChanged(val value: String) : PredictionTabEvent
    data class DateStartChanged(val value: String) : PredictionTabEvent
    data class DateEndChanged(val value: String) : PredictionTabEvent
    data class TimeStartChanged(val value: String) : PredictionTabEvent
    data class TimeEndChanged(val value: String) : PredictionTabEvent
    data class LevelChanged(val value: String) : PredictionTabEvent
    data class FormatChanged(val value: String) : PredictionTabEvent
    data class OrganizationRoleChanged(val value: String) : PredictionTabEvent
    data class TypeQueryChanged(val value: String) : PredictionTabEvent
    data class OrganizationQueryChanged(val value: String) : PredictionTabEvent
    data class TypeSelected(val value: String) : PredictionTabEvent
    data class TypeRemoved(val value: String) : PredictionTabEvent
    data class OrganizationSelected(val value: Int) : PredictionTabEvent
    data class OrganizationRemoved(val value: Int) : PredictionTabEvent
    data object SubmitClicked : PredictionTabEvent
    data object ErrorDismissed : PredictionTabEvent
}
