package com.inrotate.exast.data.prediction

import com.inrotate.exast.data.config.ApiConfig
import com.inrotate.exast.data.prediction.dto.OrganizationDto
import com.inrotate.exast.utils.logging.AppLogger
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

class OrganizationsApiClient(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig,
    private val logger: AppLogger,
) {
    suspend fun searchOrganizations(query: String): List<OrganizationDto> {
        logger.info("OrganizationsApiClient", "GET /organizations")
        val response = httpClient.get("${apiConfig.baseUrl}/organizations") {
            parameter("name", query)
        }
        return response.parseOrganizations()
    }

    private suspend fun HttpResponse.parseOrganizations(): List<OrganizationDto> {
        val responseText = bodyAsText()
        if (status.value !in 200..299) {
            logger.warn(
                "OrganizationsApiClient",
                "GET /organizations returned ${status.value}: ${responseText.take(160)}"
            )
            return emptyList()
        }

        val contentType = contentType()
        if (contentType != null && contentType.match(ContentType.Application.Json).not()) {
            logger.warn("OrganizationsApiClient", "GET /organizations returned non-json content-type: $contentType")
            return emptyList()
        }

        return runCatching {
            val root = json.parseToJsonElement(responseText)
            root.extractOrganizationArray()
                .mapNotNull { it.toOrganizationDtoOrNull() }
        }.getOrElse { exception ->
            logger.error("OrganizationsApiClient", "Cannot parse organizations response", exception)
            emptyList()
        }
    }

    private fun JsonElement.extractOrganizationArray(): JsonArray =
        when (this) {
            is JsonArray -> this
            is JsonObject -> listOf("items", "content", "data", "organizations", "results")
                .firstNotNullOfOrNull { key -> this[key] as? JsonArray }
                ?: JsonArray(emptyList())

            else -> JsonArray(emptyList())
        }

    private fun JsonElement.toOrganizationDtoOrNull(): OrganizationDto? {
        val obj = this as? JsonObject ?: return null
        val id = obj.intValue("id") ?: return null
        val name = obj.stringValue("name")
            ?: obj.stringValue("title")
            ?: obj.stringValue("fullName")
            ?: return null
        val type = obj.stringValue("type")
            ?: obj.stringValue("organizationType")
            ?: obj.stringValue("mainOrganizationType")
            ?: "OTHER"
        val isExternal = obj.booleanValue("isExternal")
            ?: obj.booleanValue("is_external")
            ?: obj.booleanValue("external")
            ?: false

        return OrganizationDto(
            id = id,
            name = name,
            type = type,
            isExternal = isExternal,
        )
    }

    private fun JsonObject.stringValue(key: String): String? =
        this[key]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() }

    private fun JsonObject.intValue(key: String): Int? =
        this[key]?.jsonPrimitive?.intOrNull

    private fun JsonObject.booleanValue(key: String): Boolean? =
        this[key]?.jsonPrimitive?.booleanOrNull

    private companion object {
        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            isLenient = true
        }
    }
}
