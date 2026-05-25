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
            location = request.location?.trim()?.takeIf { it.isNotEmpty() },
            format = request.format.trim(),
            organizationRole = request.organizationRole.trim(),
            types = request.types.distinct(),
            organizations = request.organizations.distinct(),
        )

        if (normalized.title.isBlank()) {
            return PredictionOutcome.Error(PredictionFailure.Validation("Заполните заголовок мероприятия."))
        }
        if (normalized.dateStart.isBlank()) {
            return PredictionOutcome.Error(PredictionFailure.Validation("Заполните дату начала мероприятия."))
        }

        return repository.predictEventScale(normalized)
    }

    private fun normalizeTime(value: String?): String? {
        val trimmed = value?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        return when {
            trimmed.matches(Regex("""\d{2}:\d{2}""")) -> "$trimmed:00"
            trimmed.matches(Regex("""\d{2}:\d{2}:\d{2}""")) -> trimmed
            else -> trimmed
        }
    }
}
