package com.inrotate.exast.presentation.prediction

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.inrotate.exast.domain.prediction.*
import com.inrotate.exast.utils.logging.AppLogger
import kotlinx.coroutines.*

class PredictionPresenter(
    private val predictEventScaleUseCase: PredictEventScaleUseCase,
    private val optionsRepository: PredictionOptionsRepository,
    private val logger: AppLogger,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val mutableState = mutableStateOf(PredictionTabState())
    private var organizationSearchJob: Job? = null

    val state: State<PredictionTabState> = mutableState

    init {
        loadOptions()
    }

    fun onEvent(event: PredictionTabEvent) {
        when (event) {
            is PredictionTabEvent.TitleChanged -> updateForm { copy(title = event.value) }
            is PredictionTabEvent.DescriptionChanged -> updateForm { copy(description = event.value) }
            is PredictionTabEvent.DateStartSelected -> updateForm { copy(dateStart = event.value) }
            is PredictionTabEvent.DateEndSelected -> updateForm { copy(dateEnd = event.value) }
            is PredictionTabEvent.TimeStartSelected -> updateForm { copy(timeStart = event.value) }
            is PredictionTabEvent.TimeEndSelected -> updateForm { copy(timeEnd = event.value) }
            is PredictionTabEvent.LevelSelected -> updateForm { copy(level = event.value) }
            is PredictionTabEvent.FormatSelected -> updateForm { copy(format = event.value) }
            is PredictionTabEvent.OrganizationRoleSelected -> updateForm { copy(organizationRole = event.value) }
            is PredictionTabEvent.TypeSearchChanged -> update { copy(typeQuery = event.value) }
            is PredictionTabEvent.TypeSelected -> update {
                copy(
                    selectedTypes = (selectedTypes + event.value).distinctBy { it.code },
                    typeQuery = "",
                    isPredictionStale = predictionResult != null,
                )
            }
            is PredictionTabEvent.TypeRemoved -> update {
                copy(
                    selectedTypes = selectedTypes.filterNot { it.code == event.code },
                    isPredictionStale = predictionResult != null,
                )
            }

            is PredictionTabEvent.OrganizationSearchChanged -> searchOrganizations(event.value)
            is PredictionTabEvent.OrganizationSelected -> update {
                copy(
                    selectedOrganizations = (selectedOrganizations + event.value).distinctBy { it.id },
                    organizationQuery = "",
                    organizationSuggestions = emptyList(),
                    isPredictionStale = predictionResult != null,
                )
            }
            is PredictionTabEvent.OrganizationRemoved -> update {
                copy(
                    selectedOrganizations = selectedOrganizations.filterNot { it.id == event.id },
                    isPredictionStale = predictionResult != null,
                )
            }

            PredictionTabEvent.PredictClicked -> submit()
            PredictionTabEvent.RetryClicked -> submit()
            PredictionTabEvent.ErrorDismissed -> update { copy(errorMessage = null) }
        }
    }

    private fun loadOptions() {
        scope.launch {
            logger.info("PredictionPresenter", "Loading event type options")
            val eventTypes = optionsRepository.getEventTypes()
            update { copy(typeOptions = eventTypes) }
        }
    }

    private fun searchOrganizations(query: String) {
        update { copy(organizationQuery = query) }
        organizationSearchJob?.cancel()
        if (query.length < 2) {
            update { copy(organizationsLoading = false, organizationSuggestions = emptyList()) }
            return
        }
        organizationSearchJob = scope.launch {
            delay(400)
            update { copy(organizationsLoading = true) }
            val selectedIds = mutableState.value.selectedOrganizations.map { it.id }.toSet()
            val suggestions = optionsRepository.searchOrganizations(query)
                .filterNot { it.id in selectedIds }
            update {
                copy(
                    organizationsLoading = false,
                    organizationSuggestions = suggestions,
                )
            }
        }
    }

    private fun submit() {
        val current = mutableState.value
        val validationError = validate(current)
        if (validationError != null) {
            update { copy(errorMessage = validationError, predictionResult = null) }
            return
        }

        scope.launch {
            logger.info("PredictionPresenter", "Prediction submit clicked")
            update { copy(predictionLoading = true, errorMessage = null, predictionResult = null) }
            val form = current.form
            when (
                val outcome = predictEventScaleUseCase(
                    CreatePredictionRequest(
                        title = form.title,
                        description = form.description,
                        dateStart = form.dateStart,
                        dateEnd = form.dateEnd.takeIf { it.isNotBlank() },
                        timeStart = form.timeStart.takeIf { it.isNotBlank() },
                        timeEnd = form.timeEnd.takeIf { it.isNotBlank() },
                        level = form.level,
                        format = form.format,
                        organizationRole = form.organizationRole,
                        types = current.selectedTypes.map { it.code },
                        organizations = current.selectedOrganizations,
                    )
                )
            ) {
                is PredictionOutcome.Success -> update {
                    logger.info("PredictionPresenter", "Prediction displayed")
                    copy(
                        predictionLoading = false,
                        predictionResult = outcome.result,
                        isPredictionStale = false,
                        errorMessage = null,
                    )
                }
                is PredictionOutcome.Error -> update {
                    logger.warn("PredictionPresenter", "Prediction failed: ${outcome.failure::class.simpleName}")
                    copy(predictionLoading = false, errorMessage = outcome.failure.toUserMessage())
                }
            }
        }
    }

    private fun validate(state: PredictionTabState): String? {
        val form = state.form
        if (form.title.isBlank()) return "Заполните заголовок мероприятия."
        if (form.dateStart.isBlank()) return "Выберите дату начала мероприятия."
        if (state.selectedTypes.isEmpty()) return "Выберите хотя бы один тип мероприятия."
        if (form.dateEnd.isNotBlank() && form.dateEnd < form.dateStart) {
            return "Дата окончания не может быть раньше даты начала."
        }
        if (
            (form.dateEnd.isBlank() || form.dateEnd == form.dateStart) &&
            form.timeStart.isNotBlank() &&
            form.timeEnd.isNotBlank() &&
            form.timeEnd < form.timeStart
        ) {
            return "Время окончания не может быть раньше времени начала."
        }
        if (form.organizationRole !in setOf("organization", "participation", "assistance")) {
            return "Выберите корректную роль организации."
        }
        return null
    }

    private fun PredictionFailure.toUserMessage(): String =
        when (this) {
            is PredictionFailure.Validation -> message
            is PredictionFailure.ModelNotFound -> "Модель ещё не обучена. Сначала загрузите датасет и обучите модель."
            is PredictionFailure.Network -> "Не удалось подключиться к серверу."
            is PredictionFailure.Backend -> if (statusCode == 400) "Некорректные параметры мероприятия." else message
            is PredictionFailure.Unknown -> message
        }

    private fun update(block: PredictionTabState.() -> PredictionTabState) {
        mutableState.value = mutableState.value.block()
    }

    private fun updateForm(block: PredictionFormState.() -> PredictionFormState) {
        update {
            copy(
                form = form.block(),
                isPredictionStale = predictionResult != null,
            )
        }
    }
}
