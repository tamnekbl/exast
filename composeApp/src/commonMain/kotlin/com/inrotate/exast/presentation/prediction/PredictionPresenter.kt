package com.inrotate.exast.presentation.prediction

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.inrotate.exast.domain.prediction.*
import com.inrotate.exast.utils.logging.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PredictionPresenter(
    private val predictEventScaleUseCase: PredictEventScaleUseCase,
    private val optionsRepository: PredictionOptionsRepository,
    private val logger: AppLogger,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val mutableState = mutableStateOf(PredictionTabState())

    val state: State<PredictionTabState> = mutableState

    init {
        loadOptions()
    }

    fun onEvent(event: PredictionTabEvent) {
        when (event) {
            is PredictionTabEvent.TitleChanged -> update { copy(title = event.value) }
            is PredictionTabEvent.DescriptionChanged -> update { copy(description = event.value) }
            is PredictionTabEvent.DateStartChanged -> update { copy(dateStart = event.value) }
            is PredictionTabEvent.DateEndChanged -> update { copy(dateEnd = event.value) }
            is PredictionTabEvent.TimeStartChanged -> update { copy(timeStart = event.value) }
            is PredictionTabEvent.TimeEndChanged -> update { copy(timeEnd = event.value) }
            is PredictionTabEvent.LevelChanged -> update { copy(level = event.value) }
            is PredictionTabEvent.FormatChanged -> update { copy(format = event.value) }
            is PredictionTabEvent.OrganizationRoleChanged -> update { copy(organizationRole = event.value) }
            is PredictionTabEvent.TypeQueryChanged -> update { copy(typeQuery = event.value) }
            is PredictionTabEvent.OrganizationQueryChanged -> update { copy(organizationQuery = event.value) }
            is PredictionTabEvent.TypeSelected -> update {
                copy(selectedTypes = (selectedTypes + event.value).distinct(), typeQuery = "")
            }

            is PredictionTabEvent.TypeRemoved -> update {
                copy(selectedTypes = selectedTypes - event.value)
            }

            is PredictionTabEvent.OrganizationSelected -> update {
                copy(selectedOrganizations = (selectedOrganizations + event.value).distinct(), organizationQuery = "")
            }

            is PredictionTabEvent.OrganizationRemoved -> update {
                copy(selectedOrganizations = selectedOrganizations - event.value)
            }

            PredictionTabEvent.SubmitClicked -> submit()
            PredictionTabEvent.ErrorDismissed -> update { copy(errorMessage = null) }
        }
    }

    private fun loadOptions() {
        scope.launch {
            logger.info("PredictionPresenter", "Loading prediction options")
            val types = optionsRepository.getEventTypes()
            val organizations = optionsRepository.getOrganizations()
            update { copy(typeOptions = types, organizationOptions = organizations) }
        }
    }

    private fun submit() {
        val current = mutableState.value
        if (!current.canSubmit) return
        scope.launch {
            logger.info("PredictionPresenter", "Prediction submit clicked")
            update { copy(isLoading = true, errorMessage = null, result = null) }
            when (
                val outcome = predictEventScaleUseCase(
                    CreatePredictionRequest(
                        title = current.title,
                        description = current.description,
                        dateStart = current.dateStart,
                        dateEnd = current.dateEnd,
                        timeStart = current.timeStart,
                        timeEnd = current.timeEnd,
                        level = current.level,
                        location = null,
                        format = current.format,
                        organizationRole = current.organizationRole,
                        types = current.selectedTypes,
                        organizations = current.selectedOrganizations,
                    )
                )
            ) {
                is PredictionOutcome.Success -> update {
                    logger.info("PredictionPresenter", "Prediction displayed")
                    copy(isLoading = false, result = outcome.result, errorMessage = null)
                }

                is PredictionOutcome.Error -> update {
                    logger.warn("PredictionPresenter", "Prediction failed: ${outcome.failure::class.simpleName}")
                    copy(isLoading = false, errorMessage = outcome.failure.toUserMessage())
                }
            }
        }
    }

    private fun PredictionFailure.toUserMessage(): String =
        when (this) {
            is PredictionFailure.Validation -> message
            is PredictionFailure.ModelNotFound -> "Модель ещё не обучена. Сначала запустите обучение модели."
            is PredictionFailure.Network -> "Не удалось получить прогноз. Проверьте доступность сервера."
            is PredictionFailure.Backend -> message
            is PredictionFailure.Unknown -> message
        }

    private fun update(block: PredictionTabState.() -> PredictionTabState) {
        mutableState.value = mutableState.value.block()
    }
}
