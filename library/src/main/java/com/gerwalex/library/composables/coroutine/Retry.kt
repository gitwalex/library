package com.gerwalex.library.coroutine

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay


/**
 * Retries a given [block] of code up to [times] times, with an exponential backoff delay between attempts.
 *
 * This function attempts to execute the provided [block] and, if it throws an exception, it retries
 * the execution after a delay. The delay increases exponentially with each retry, up to a maximum delay.
 * If the [block] succeeds, the result is returned immediately. If all retries fail, the last exception
 * thrown is re-thrown. If a [CancellationException] is caught, the function returns null.
 *
 * @param T The type of the result returned by the [block].
 * @param times The maximum number of times to retry the [block]. Defaults to 3. Must be greater than 0.
 * @param initialDelay The initial delay in milliseconds before the first retry. Defaults to 1000 (1 second).
 * @param maxDelay The maximum delay in milliseconds between retries. Defaults to 10000 (10 seconds).
 * @param factor The exponential factor to increase the delay with each retry. Defaults to 2.0.
 * @param block The block of code to execute and potentially retry.
 * @return The result of the [block] if it succeeds, or null if a [CancellationException] is caught.
 * @throws Exception If the [block] fails after all retry attempts, the last exception thrown is re-thrown unless it is CancellationException.
 *
 * @sample
 * ```kotlin
 * suspend fun fetchData(): String {
 *     // Simulates a network request that might fail
 *     if (Random.nextBoolean()) {
 *         throw IOException("Network error")
 *     }
 *     return "Data"
 * }
 *
 * suspend fun main() {
 *      val data = retry(times = 5) { fetchData() }
 *      println("Data: $data")
 * }
 * ```
 */
suspend fun <T> retry(
    times: Int = 3,
    initialDelay: Long = 1000,
    maxDelay: Long = 10000,
    factor: Double = 2.0,
    block: suspend () -> T
): T? {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (e: Exception) {
            if (e is CancellationException) return null
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return block() // One last attempt without delay
}