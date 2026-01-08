package com.gerwalex.library.ext

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/**
 * Executes an HTTP request safely, handling common exceptions and wrapping the result in an [ApiResponse].
 * This function simplifies network calls by catching Ktor's [ResponseException] (both client and server),
 * [IOException] for network issues, and [SerializationException] for parsing errors.
 *
 * It returns a sealed class [ApiResponse] which is either [ApiResponse.Success] containing the
 * deserialized body, or one of the [ApiResponse.Error] subtypes.
 *
 * @param T The type of the successful response body.
 * @param E The type of the error response body.
 * @param block A lambda with receiver [HttpRequestBuilder] to configure the request (e.g., URL, method, headers, body).
 * @return An [ApiResponse] which is one of:
 * - [ApiResponse.Success] with the deserialized body of type [T] on a successful (2xx) response.
 * - [ApiResponse.Error.HttpError] on a client (4xx) or server (5xx) error, containing the status code and an optional deserialized error body of type [E].
 * - [ApiResponse.Error.NetworkError] when an [IOException] occurs, indicating a connectivity problem.
 * - [ApiResponse.Error.SerializationError] when the response body cannot be deserialized into the expected type [T].
 */
suspend inline fun <reified T, reified E> HttpClient.safeRequest(
    block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T, E> =
    try {
        val response = request { block() }
        ApiResponse.Success(response.body())
    } catch (e: ClientRequestException) {
        ApiResponse.Error.HttpError(e.response.status.value, e.errorBody())
    } catch (e: ServerResponseException) {
        ApiResponse.Error.HttpError(e.response.status.value, e.errorBody())
    } catch (e: IOException) {
        ApiResponse.Error.NetworkError
    } catch (e: SerializationException) {
        ApiResponse.Error.SerializationError
    }

/**
 * Tries to parse the error response body into a specific type [E].
 * @return The parsed error body of type [E], or null if parsing fails (e.g., due to a [SerializationException]).
 */
suspend inline fun <reified E> ResponseException.errorBody(): E? =
    try {
        response.body()
    } catch (e: SerializationException) {
        null
    }

sealed class ApiResponse<out T, out E> {
    /**
     * Represents successful network responses (2xx).
     */
    data class Success<T>(val body: T) : ApiResponse<T, Nothing>()

    sealed class Error<E> : ApiResponse<Nothing, E>() {
        /**
         * Represents server (50x) and client (40x) errors.
         */
        data class HttpError<E>(val code: Int, val errorBody: E?) : Error<E>()

        /**
         * Represent IOExceptions and connectivity issues.
         */
        object NetworkError : Error<Nothing>()

        /**
         * Represent SerializationExceptions.
         */
        object SerializationError : Error<Nothing>()
    }
}