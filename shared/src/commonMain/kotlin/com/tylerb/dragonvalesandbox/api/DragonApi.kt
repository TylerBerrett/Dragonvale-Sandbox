package com.tylerb.dragonvalesandbox.api

import com.tylerb.dragonvalesandbox.model.*
import com.tylerb.dragonvalesandbox.util.jsonIgnore
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class DragonApi {

    companion object {
        private const val BASE_URL = "https://dvbox2cdn.bin.sh"
        private const val DRAGONS = "/data/dragons.json"
    }


    private val httpClient = HttpClient {
        expectSuccess = true
        defaultRequest {
            url(BASE_URL)
        }
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }


    @Throws(Exception::class)
    suspend fun getDragonList(): List<DragonData> {
        val body = httpClient.get(DRAGONS).bodyAsText()
        val dragonResponse = jsonIgnore.decodeFromString(DragonResponseSerializer, body)
        return dragonResponse.dragons
    }



}