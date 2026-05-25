package com.inrotate.exast.data.prediction

import com.inrotate.exast.data.prediction.dto.OrganizationDto
import com.inrotate.exast.data.prediction.dto.PredictionRequestDto
import com.inrotate.exast.data.prediction.dto.PredictionResponseDto
import com.inrotate.exast.data.prediction.dto.SimilarEventDto
import com.inrotate.exast.domain.prediction.CreatePredictionRequest
import com.inrotate.exast.domain.prediction.OrganizationOption
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
        format = format,
        organizationRole = organizationRole,
        types = types,
        organizations = organizations.map { it.id },
    )

fun OrganizationOption.toDto(): OrganizationDto =
    OrganizationDto(
        id = id,
        name = name,
        type = type,
        isExternal = isExternal,
    )

fun OrganizationDto.toDomain(): OrganizationOption =
    OrganizationOption(
        id = id,
        name = name,
        type = type,
        isExternal = isExternal,
    )

fun PredictionResponseDto.toDomain(): PredictionResult =
    PredictionResult(
        predictedScale = predictedScale,
        scaleDescription = scaleDescription ?: description ?: "",
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
