package com.inrotate.exast.presentation.prediction

import com.inrotate.exast.domain.prediction.OrganizationOption
import com.inrotate.exast.domain.prediction.PredictionOption

sealed interface PredictionTabEvent {
    data class TitleChanged(val value: String) : PredictionTabEvent
    data class DescriptionChanged(val value: String) : PredictionTabEvent
    data class DateStartSelected(val value: String) : PredictionTabEvent
    data class DateEndSelected(val value: String) : PredictionTabEvent
    data class TimeStartSelected(val value: String) : PredictionTabEvent
    data class TimeEndSelected(val value: String) : PredictionTabEvent
    data class LevelSelected(val value: String) : PredictionTabEvent
    data class FormatSelected(val value: String) : PredictionTabEvent
    data class OrganizationRoleSelected(val value: String) : PredictionTabEvent
    data class TypeSearchChanged(val value: String) : PredictionTabEvent
    data class TypeSelected(val value: PredictionOption) : PredictionTabEvent
    data class TypeRemoved(val code: String) : PredictionTabEvent
    data class OrganizationSearchChanged(val value: String) : PredictionTabEvent
    data class OrganizationSelected(val value: OrganizationOption) : PredictionTabEvent
    data class OrganizationRemoved(val id: Int) : PredictionTabEvent
    data object PredictClicked : PredictionTabEvent
    data object RetryClicked : PredictionTabEvent
    data object ErrorDismissed : PredictionTabEvent
}
