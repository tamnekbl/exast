package com.inrotate.exast.data.prediction

import com.inrotate.exast.data.prediction.dto.PredictionRequestDto
import com.inrotate.exast.data.prediction.dto.PredictionResponseDto
import com.inrotate.exast.data.prediction.dto.SimilarEventDto
import com.inrotate.exast.domain.prediction.CreatePredictionRequest
import com.inrotate.exast.domain.prediction.PredictionResult
import com.inrotate.exast.domain.prediction.SimilarEvent

fun CreatePredictionRequest.toDto(): PredictionRequestDto =
    PredictionRequestDto(
        title = title,
        description = description,
        dateStart = dateStart,
        dateEnd = dateEnd,
        timeStart = timeStart,
        timeEnd = timeEnd,
        level = level,
        location = location,
        format = format,
        organizationRole = organizationRole,
        types = types,
        organizations = organizations,
    )

fun PredictionResponseDto.toDomain(): PredictionResult =
    PredictionResult(
        predictedScale = predictedScale,
        scaleDescription = scaleDescription,
        participantsRange = participantsRange,
        probabilities = probabilities,
        confidence = confidence,
        similarEvents = similarEvents.map { it.toDomain() },
        modelVersion = modelVersion,
        modelTrainedAt = modelTrainedAt,
        warnings = warnings,
    )

private fun SimilarEventDto.toDomain(): SimilarEvent =
    SimilarEvent(
        title = title,
        description = description,
        dateStart = dateStart,
        dateEnd = dateEnd,
        level = level,
        format = format,
        organizationRole = organizationRole,
        mainType = mainType,
        mainOrganizationType = mainOrganizationType,
        participantsTotal = participantsTotal,
        eventScale = eventScale,
        similarity = similarity,
    )
