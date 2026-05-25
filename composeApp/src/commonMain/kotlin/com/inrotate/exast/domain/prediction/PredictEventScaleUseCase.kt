package com.inrotate.exast.domain.prediction

class PredictEventScaleUseCase(
    private val repository: PredictionRepository,
) {
    suspend operator fun invoke(request: CreatePredictionRequest): PredictionOutcome {
        val normalized = request.copy(
            title = request.title.trim(),
            description = request.description?.trim()?.takeIf { it.isNotEmpty() },
            dateStart = request.dateStart.trim(),
            dateEnd = request.dateEnd?.trim()?.takeIf { it.isNotEmpty() },
            timeStart = normalizeTime(request.timeStart),
            timeEnd = normalizeTime(request.timeEnd),
            level = request.level.trim(),
            format = request.format.trim(),
            organizationRole = request.organizationRole.trim(),
            types = request.types.distinct(),
            organizations = request.organizations.distinctBy { it.id },
        )

        validate(normalized)?.let { return PredictionOutcome.Error(PredictionFailure.Validation(it)) }

        return repository.predictEventScale(normalized)
    }

    private fun validate(request: CreatePredictionRequest): String? {
        if (request.title.isBlank()) return "Заполните заголовок мероприятия."
        if (request.dateStart.isBlank()) return "Выберите дату начала мероприятия."
        if (request.level.isBlank()) return "Выберите уровень мероприятия."
        if (request.format !in allowedFormats) return "Выберите корректный формат мероприятия."
        if (request.organizationRole !in allowedOrganizationRoles) return "Выберите корректную роль организации."
        if (request.types.isEmpty()) return "Выберите хотя бы один тип мероприятия."
        request.dateEnd?.let {
            if (it < request.dateStart) return "Дата окончания не может быть раньше даты начала."
        }
        if (
            request.dateEnd?.let { it == request.dateStart } != false &&
            request.timeStart != null &&
            request.timeEnd != null &&
            request.timeEnd < request.timeStart
        ) {
            return "Время окончания не может быть раньше времени начала."
        }
        return null
    }

    private fun normalizeTime(value: String?): String? {
        val trimmed = value?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        return when {
            trimmed.matches(Regex("""\d{2}:\d{2}""")) -> "$trimmed:00"
            trimmed.matches(Regex("""\d{2}:\d{2}:\d{2}""")) -> trimmed
            else -> trimmed
        }
    }

    private companion object {
        val allowedFormats = setOf("online", "offline", "hybrid")
        val allowedOrganizationRoles = setOf("organization", "participation", "assistance")
    }
}
