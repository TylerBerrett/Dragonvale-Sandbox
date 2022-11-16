package com.tylerb.dragonvalesandbox.api

import com.tylerb.dragonvalesandbox.model.*
import com.tylerb.dragonvalesandbox.util.jsonIgnore
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

internal class DragonApi {

    companion object {
        private const val DRAGONS_URL = "https://dvbox2cdn.bin.sh/data/dragons.json"
        private const val GITHUB_URL = "https://api.github.com/repos/drouu/dragonvale-sandbox/commits"
    }


    private val httpClient = HttpClient {
        expectSuccess = true
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
        val body = httpClient.get(DRAGONS_URL).bodyAsText()
        val dragonResponse = jsonIgnore.decodeFromString(DragonResponseSerializer, body)
        return dragonResponse.dragons
    }

    @Throws(Exception::class)
    suspend fun checkForUpdates(): String {
        val body = httpClient.get(GITHUB_URL) {
            parameter("path", "dragons.js")
            parameter("page", 1)
            parameter("per_page", 1)
        }.body<List<GithubResponse>>()
        return body.first().commit.committer.date
    }



}