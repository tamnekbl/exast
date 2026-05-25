package com.inrotate.exast.domain.prediction

data class CreatePredictionRequest(
    val title: String,
    val description: String?,
    val dateStart: String,
    val dateEnd: String?,
    val timeStart: String?,
    val timeEnd: String?,
    val level: String,
    val format: String,
    val organizationRole: String,
    val types: List<String>,
    val organizations: List<OrganizationOption>,
)
