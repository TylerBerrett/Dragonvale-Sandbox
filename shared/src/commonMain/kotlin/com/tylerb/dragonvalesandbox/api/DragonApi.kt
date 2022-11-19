package com.tylerb.dragonvalesandbox.api

import com.tylerb.dragonvalesandbox.model.*
import com.tylerb.dragonvalesandbox.util.jsonIgnore
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.logging.*
import io.ktor.utils.io.*
import io.ktor.utils.io.bits.*
import io.ktor.utils.io.core.internal.*
import kotlinx.coroutines.*
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

    // Can be used to cache images but takes up awhile to do so
    @Throws(Exception::class)
    suspend fun getImageData(dragons: List<DragonData>) = coroutineScope {

        suspend fun getByteArray(url: String): ByteArray {
            val response = httpClient.get(url)
            val byteArray = ByteArray(response.contentLength()!!.toInt())
            response.bodyAsChannel().readFully(byteArray)
            return byteArray
        }

        val flagsDone = mutableMapOf<String, ByteArray>()
        dragons.map { dragon ->
            async {
                val imageUrl = async { getByteArray(dragon.imageUrl) }
                val eggUrl = async { getByteArray(dragon.eggIconUrl) }
                val flagUrls = async {
                    dragon.flagImageUrls.map {
                        async {
                            flagsDone[it] ?: kotlin.run {
                                flagsDone[it] = getByteArray(it)
                                flagsDone[it]!!
                            }
                        }
                    }.awaitAll()
                }

                mapOf(
                    dragon.name to DragonImage(
                        imageUrl.await(),
                        eggUrl.await(),
                        flagUrls.await()
                    )
                )
            }
        }.awaitAll()
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