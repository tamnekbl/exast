package com.inrotate.exast.data.prediction

import com.inrotate.exast.data.config.ApiConfig
import com.inrotate.exast.data.prediction.dto.BackendErrorDto
import com.inrotate.exast.data.prediction.dto.PredictionRequestDto
import com.inrotate.exast.data.prediction.dto.PredictionResponseDto
import com.inrotate.exast.data.prediction.dto.SimilarEventDto
import com.inrotate.exast.utils.logging.AppLogger
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

class PredictionApiClient(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig,
    private val logger: AppLogger,
) {
    suspend fun postPredictAttendance(dto: PredictionRequestDto): PredictionResponseDto {
        logger.info("PredictionApiClient", "POST /analytics/predict-attendance")
        val response = httpClient.post("${apiConfig.baseUrl}/analytics/predict-attendance") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        val body = response.bodyAsText()
        if (response.status.value !in 200..299) {
            throw PredictionApiException(response.status.value, body.backendErrorMessage())
        }
        return parsePredictionResponse(body)
    }

    private fun parsePredictionResponse(body: String): PredictionResponseDto {
        val root = runCatching { json.parseToJsonElement(body) }.getOrElse { exception ->
            throw PredictionApiException(null, "Не удалось разобрать ответ backend.", exception)
        }
        val rootObject = root as? JsonObject
            ?: throw PredictionApiException(null, "Backend вернул некорректный ответ прогноза.")

        rootObject.backendErrorMessageOrNull()?.let { message ->
            throw PredictionApiException(null, message)
        }

        val payload = rootObject.extractPayload()
        val predictedScale = payload.stringValue("predictedScale", "predicted_scale")
            ?: throw PredictionApiException(null, "Backend не вернул predictedScale.")
        val participantsRange = payload.stringValue("participantsRange", "participants_range")
            ?: scaleRange(predictedScale)
        val confidence = payload.doubleValue("confidence") ?: 0.0
        val modelVersion = payload.stringValue("modelVersion", "model_version") ?: ""

        return PredictionResponseDto(
            predictedScale = predictedScale,
            scaleDescription = payload.stringValue("scaleDescription", "scale_description"),
            description = payload.stringValue("description"),
            participantsRange = participantsRange,
            probabilities = payload.probabilities(),
            confidence = confidence,
            similarEvents = payload.similarEvents(),
            modelVersion = modelVersion,
            modelTrainedAt = payload.stringValue("modelTrainedAt", "model_trained_at"),
            warnings = payload.stringArray("warnings"),
        )
    }

    private fun JsonObject.extractPayload(): JsonObject =
        listOf("result", "data", "prediction", "response")
            .firstNotNullOfOrNull { key -> this[key] as? JsonObject }
            ?: this

    private fun JsonObject.probabilities(): Map<String, Double> {
        val source = firstObject("probabilities", "probability")
            ?: return emptyMap()
        return source.mapValues { (_, value) -> value.jsonPrimitive.doubleOrNull ?: 0.0 }
    }

    private fun JsonObject.similarEvents(): List<SimilarEventDto> {
        val source = firstArray("similarEvents", "similar_events") ?: return emptyList()
        return source.mapNotNull { item ->
            val obj = item as? JsonObject ?: return@mapNotNull null
            SimilarEventDto(
                title = obj.stringValue("title"),
                description = obj.stringValue("description"),
                dateStart = obj.stringValue("dateStart", "date_start"),
                dateEnd = obj.stringValue("dateEnd", "date_end"),
                level = obj.stringValue("level"),
                format = obj.stringValue("format"),
                organizationRole = obj.stringValue("organizationRole", "organization_role"),
                mainType = obj.stringValue("mainType", "main_type"),
                mainOrganizationType = obj.stringValue("mainOrganizationType", "main_organization_type"),
                participantsTotal = obj.intValue("participantsTotal", "participants_total"),
                eventScale = obj.stringValue("eventScale", "event_scale") ?: "",
                similarity = obj.doubleValue("similarity") ?: 0.0,
            )
        }
    }

    private fun JsonObject.stringArray(vararg keys: String): List<String> =
        firstArray(*keys)
            ?.mapNotNull { it.jsonPrimitive.content.takeIf(String::isNotBlank) }
            ?: emptyList()

    private fun JsonObject.firstArray(vararg keys: String): JsonArray? =
        keys.firstNotNullOfOrNull { key -> this[key] as? JsonArray }

    private fun JsonObject.firstObject(vararg keys: String): JsonObject? =
        keys.firstNotNullOfOrNull { key -> this[key] as? JsonObject }

    private fun JsonObject.stringValue(vararg keys: String): String? =
        keys.firstNotNullOfOrNull { key ->
            this[key]?.jsonPrimitive?.content?.takeIf(String::isNotBlank)
        }

    private fun JsonObject.intValue(vararg keys: String): Int? =
        keys.firstNotNullOfOrNull { key -> this[key]?.jsonPrimitive?.intOrNull }

    private fun JsonObject.doubleValue(vararg keys: String): Double? =
        keys.firstNotNullOfOrNull { key -> this[key]?.jsonPrimitive?.doubleOrNull }

    private fun String.backendErrorMessage(): String =
        runCatching {
            val error = json.decodeFromString<BackendErrorDto>(this)
            error.message ?: error.error ?: error.code ?: this
        }.getOrElse { this }

    private fun JsonObject.backendErrorMessageOrNull(): String? {
        val marker = stringValue("error", "code") ?: return null
        return stringValue("message") ?: marker
    }

    private fun scaleRange(code: String): String =
        when (code) {
            "small_1_20" -> "1-20"
            "medium_21_50" -> "21-50"
            "large_51_200" -> "51-200"
            "mass_201_plus" -> "201+"
            else -> ""
        }

    private companion object {
        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            isLenient = true
        }
    }
}

class PredictionApiException(
    val statusCode: Int?,
    override val message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
