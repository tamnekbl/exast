package com.inrotate.exast.data.prediction

import com.inrotate.exast.data.prediction.dto.BackendErrorDto
import com.inrotate.exast.domain.prediction.CreatePredictionRequest
import com.inrotate.exast.domain.prediction.PredictionFailure
import com.inrotate.exast.domain.prediction.PredictionOutcome
import com.inrotate.exast.domain.prediction.PredictionRepository
import com.inrotate.exast.utils.logging.AppLogger
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.network.*
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

class PredictionRepositoryImpl(
    private val apiClient: PredictionApiClient,
    private val logger: AppLogger,
) : PredictionRepository {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun predictEventScale(request: CreatePredictionRequest): PredictionOutcome =
        try {
            val result = apiClient.postPredictAttendance(request.toDto()).toDomain()
            logger.info(
                tag = "PredictionRepository",
                message = "Prediction received: scale=${result.predictedScale}, confidence=${result.confidence}",
            )
            PredictionOutcome.Success(result)
        } catch (exception: ClientRequestException) {
            logger.warn("PredictionRepository", "Backend client error: ${exception.response.status.value}")
            handleResponseException(exception)
        } catch (exception: ServerResponseException) {
            logger.warn("PredictionRepository", "Backend server error: ${exception.response.status.value}")
            handleResponseException(exception)
        } catch (exception: UnresolvedAddressException) {
            logger.error("PredictionRepository", "Backend address is unavailable", exception)
            PredictionOutcome.Error(PredictionFailure.Network)
        } catch (exception: IOException) {
            logger.error("PredictionRepository", "Network error while requesting prediction", exception)
            PredictionOutcome.Error(PredictionFailure.Network)
        } catch (exception: Exception) {
            logger.error("PredictionRepository", "Unexpected prediction error", exception)
            PredictionOutcome.Error(PredictionFailure.Unknown("Не удалось обработать ответ сервера."))
        }

    private suspend fun handleResponseException(exception: ResponseException): PredictionOutcome {
        val status = exception.response.status
        val message = backendMessage(exception)
        return when {
            status == HttpStatusCode.Conflict || message.contains("MODEL_NOT_FOUND", ignoreCase = true) ->
                PredictionOutcome.Error(PredictionFailure.ModelNotFound)

            status == HttpStatusCode.BadRequest ->
                PredictionOutcome.Error(PredictionFailure.Backend(status.value, message))

            else ->
                PredictionOutcome.Error(PredictionFailure.Backend(status.value, message))
        }
    }

    private suspend fun backendMessage(exception: ResponseException): String {
        val body = exception.response.bodyAsText()
        if (body.isBlank()) return exception.message ?: "Ошибка backend."
        val dto = runCatching { json.decodeFromString<BackendErrorDto>(body) }.getOrNull()
        return dto?.message ?: dto?.error ?: dto?.code ?: body
    }
}
