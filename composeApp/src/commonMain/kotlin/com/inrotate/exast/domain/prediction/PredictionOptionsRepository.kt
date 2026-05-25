package com.inrotate.exast.domain.prediction

interface PredictionOptionsRepository {
    suspend fun getEventTypes(): List<PredictionOption>
    suspend fun searchOrganizations(query: String): List<OrganizationOption>
}
