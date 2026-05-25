package com.inrotate.exast.data.prediction

import com.inrotate.exast.domain.prediction.OrganizationOption
import com.inrotate.exast.domain.prediction.PredictionOption
import com.inrotate.exast.domain.prediction.PredictionOptionsRepository
import com.inrotate.exast.utils.logging.AppLogger
import io.ktor.client.plugins.*
import io.ktor.util.network.*
import kotlinx.io.IOException

class PredictionOptionsRepositoryImpl(
    private val organizationsApiClient: OrganizationsApiClient,
    private val logger: AppLogger,
) : PredictionOptionsRepository {
    override suspend fun getEventTypes(): List<PredictionOption> =
        eventTypes

    override suspend fun searchOrganizations(query: String): List<OrganizationOption> {
        if (query.isBlank()) return emptyList()
        return try {
            organizationsApiClient.searchOrganizations(query.trim()).map { it.toDomain() }
        } catch (exception: ClientRequestException) {
            logger.warn(
                "PredictionOptionsRepository",
                "Organization search client error: ${exception.response.status.value}"
            )
            emptyList()
        } catch (exception: ServerResponseException) {
            logger.warn(
                "PredictionOptionsRepository",
                "Organization search server error: ${exception.response.status.value}"
            )
            emptyList()
        } catch (exception: UnresolvedAddressException) {
            logger.error("PredictionOptionsRepository", "Organization endpoint is unavailable", exception)
            emptyList()
        } catch (exception: IOException) {
            logger.error("PredictionOptionsRepository", "Organization search network error", exception)
            emptyList()
        }
    }

    private companion object {
        val eventTypes = listOf(
            PredictionOption("cultural_creative", "Культурно-творческое"),
            PredictionOption("physical", "Физкультурно-спортивное"),
            PredictionOption("scientefic_educational", "Научно-образовательное"),
            PredictionOption("patriotic", "Патриотическое"),
            PredictionOption("student_self_government", "Студенческое самоуправление"),
            PredictionOption("volunteering", "Волонтерское"),
            PredictionOption("civic", "Гражданско-правовое"),
            PredictionOption("professional_labor", "Профессионально-трудовое"),
            PredictionOption("spiritual_moral", "Духовно-нравственное"),
            PredictionOption("ecological", "Экологическое"),
            PredictionOption("project_entrepreneurial", "Проектно-предпринимательское"),
        )
    }
}
