package com.github.joechung2008.diagnostics.services

import com.github.joechung2008.diagnostics.models.Diagnostics
import com.github.joechung2008.diagnostics.models.AzureEnvironment
import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

suspend fun <T> CompletableFuture<T>.await(): T = suspendCancellableCoroutine { cont ->
    whenComplete { result, exception ->
        if (exception == null) {
            cont.resume(result)
        } else {
            cont.resumeWithException(exception)
        }
    }
    cont.invokeOnCancellation { this.cancel(true) }
}

object DiagnosticsFetcher {
    private val httpClient: HttpClient = HttpClient.newBuilder().build()
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Fetch diagnostics from the Azure environment's diagnostics endpoint.
     * Throws an Exception when the HTTP request fails or the response cannot be parsed.
     */
    suspend fun fetchDiagnostics(environment: AzureEnvironment): Diagnostics {
        val url = environment.getDiagnosticsApiUrl()
        return fetchDiagnostics(url)
    }

    /**
     * Fetch diagnostics from the provided URL.
     * Throws an Exception when the HTTP request fails or the response cannot be parsed.
     */
    suspend fun fetchDiagnostics(url: String): Diagnostics {
        if (url.isBlank()) {
            throw IllegalArgumentException("URL must be provided")
        }
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build()
        try {
            val response = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).await()
            if (response.statusCode() !in 200..299) {
                throw IOException("HTTP ${response.statusCode()} while fetching diagnostics from $url. Response body: ${response.body()}")
            }
            try {
                val diagnostics = json.decodeFromString(Diagnostics.serializer(), response.body())
                return diagnostics
            } catch (ex: SerializationException) {
                throw Exception("Failed to deserialize diagnostics JSON from $url: ${ex.message}", ex)
            }
        } catch (ex: IOException) {
            throw Exception("HTTP request failed for $url: ${ex.message}", ex)
        } catch (ex: InterruptedException) {
            throw Exception("HTTP request interrupted for $url: ${ex.message}", ex)
        }
    }
}
