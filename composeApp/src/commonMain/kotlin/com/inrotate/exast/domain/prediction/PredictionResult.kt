package com.inrotate.exast.domain.prediction

data class PredictionResult(
    val predictedScale: String,
    val scaleDescription: String,
    val participantsRange: String,
    val probabilities: Map<String, Double>,
    val confidence: Double,
    val similarEvents: List<SimilarEvent>,
    val modelVersion: String,
    val modelTrainedAt: String?,
    val warnings: List<String>,
)

data class SimilarEvent(
    val title: String?,
    val description: String?,
    val dateStart: String?,
    val dateEnd: String?,
    val level: String?,
    val format: String?,
    val organizationRole: String?,
    val mainType: String?,
    val mainOrganizationType: String?,
    val participantsTotal: Int?,
    val eventScale: String,
    val similarity: Double,
)
