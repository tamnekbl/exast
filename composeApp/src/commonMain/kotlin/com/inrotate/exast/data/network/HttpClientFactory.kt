package com.inrotate.exast.data.network

import com.inrotate.exast.data.config.ApiConfig
import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun createHttpClient(apiConfig: ApiConfig): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }
        )
    }
    if (!apiConfig.username.isNullOrBlank() && apiConfig.password != null) {
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(
                        username = apiConfig.username.orEmpty(),
                        password = apiConfig.password.orEmpty(),
                    )
                }
                sendWithoutRequest { true }
            }
        }
    }
}
