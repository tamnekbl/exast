package com.inrotate.exast.data.prediction.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class PredictionRequestDto(
    val title: String,
    val description: String? = null,
    val dateStart: String,
    val dateEnd: String? = null,
    val timeStart: String? = null,
    val timeEnd: String? = null,
    val level: String,
    val format: String,
    val organizationRole: String,
    val types: List<String>,
    val organizations: List<Int>,
)

@Serializable
data class OrganizationDto(
    val id: Int,
    val name: String,
    val type: String,
    val isExternal: Boolean,
)

@Serializable
data class PredictionResponseDto(
    val predictedScale: String,
    val scaleDescription: String? = null,
    val description: String? = null,
    val participantsRange: String,
    val probabilities: Map<String, Double> = emptyMap(),
    val confidence: Double,
    val similarEvents: List<SimilarEventDto> = emptyList(),
    val modelVersion: String,
    val modelTrainedAt: String? = null,
    val metrics: JsonObject? = null,
    val warnings: List<String> = emptyList(),
)

@Serializable
data class SimilarEventDto(
    val title: String? = null,
    val description: String? = null,
    val dateStart: String? = null,
    val dateEnd: String? = null,
    val level: String? = null,
    val format: String? = null,
    val organizationRole: String? = null,
    val mainType: String? = null,
    val mainOrganizationType: String? = null,
    val participantsTotal: Int? = null,
    val eventScale: String,
    val similarity: Double,
)

@Serializable
data class BackendErrorDto(
    val code: String? = null,
    val message: String? = null,
    val error: String? = null,
    val details: JsonObject? = null,
)
