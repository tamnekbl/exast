package com.inrotate.exast.data.prediction

import com.inrotate.exast.data.config.ApiConfig
import com.inrotate.exast.data.prediction.dto.PredictionRequestDto
import com.inrotate.exast.data.prediction.dto.PredictionResponseDto
import com.inrotate.exast.utils.logging.AppLogger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class PredictionApiClient(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig,
    private val logger: AppLogger,
) {
    suspend fun postPredictAttendance(dto: PredictionRequestDto): PredictionResponseDto {
        logger.info("PredictionApiClient", "POST /analytics/predict-attendance")
        return httpClient.post("${apiConfig.baseUrl}/analytics/predict-attendance") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body()
    }
}
