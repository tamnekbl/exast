package com.inrotate.exast.domain.prediction

interface PredictionOptionsRepository {
    suspend fun getEventTypes(): List<PredictionOption>
    suspend fun getOrganizations(): List<OrganizationOption>
}
