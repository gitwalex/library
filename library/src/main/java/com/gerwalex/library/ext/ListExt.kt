package com.gerwalex.library.ext

/**
 * Returns the second element.
 * @throws NoSuchElementException if the list is empty or has only one element.
 */
fun <T> List<T>.second(): T {
    if (size < 2) throw NoSuchElementException("List has less than 2 elements")
    return this[1]
}

/**
 * Returns the second element, or `null` if the list is empty or has only one element.
 */
fun <T> List<T>.secondOrNull(): T? {
    return if (size >= 2) this[1] else null
}

/**
 * Returns the list if it is not empty, or `null` if it is empty.
 * This is useful for chaining calls on a list that might be empty.
 *
 * @return The original list if it contains one or more elements, otherwise `null`.
 * @receiver List<T> The list to check.
 * @sample com.gerwalex.library.ext.ListExtTest.takeIfNotEmptyTest
 */
fun <T> List<T>.takeIfNotEmpty(): List<T>? {
    return ifEmpty { null }
}

/**
 * Splits a list into a list of lists, each not exceeding the specified size.
 *
 * @param chunkSize The desired size of each sublist. Must be a positive integer.
 * @return A list of lists, where each inner list is a portion of the original list.
 *         The last sublist may be smaller than [chunkSize] if the original list's size
 *         is not a multiple of [chunkSize].
 * @throws IllegalArgumentException if [chunkSize] is not positive.
 */
fun <T> List<T>.split(predicate: (T) -> Boolean): Pair<List<T>, List<T>> {
    return partition(predicate)
}

/**
 * Returns the sum of all values produced by [selector] function applied to each element in the collection
 * as a [Long].
 */
fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    return fold(0L) { sum, element -> sum + selector(element) }
}

/**
 * Replaces all occurrences of a specified value in a mutable list with another value.
 *
 * @param oldValue The value to be replaced.
 * @param newValue The value to replace with.
 * @receiver MutableList<T> The list in which to perform the replacement.
 */
fun <T> List<T>.replaceAll(oldValue: T, newValue: T): List<T> {
    return map { if (it == oldValue) newValue else it }
}

/**
 * Splits the original collection into a list of lists, where the start of each new list is determined by the `predicate`.
 *
 * The predicate is evaluated for each element. A new chunk is started *before* the element for which the predicate returns `true`.
 *
 * @param predicate A function that returns `true` to indicate the start of a new chunk.
 * @return A list of lists, where each inner list represents a chunk of the original list.
 *
 * @sample com.gerwalex.library.ext.ListExtTest.chunkedByTest
 */
fun <T> List<T>.chunkedBy(predicate: (T) -> Boolean): List<List<T>> {
    val result = mutableListOf<List<T>>()
    var currentChunk = mutableListOf<T>()

    forEach { item ->
        if (predicate(item) && currentChunk.isNotEmpty()) {
            result.add(currentChunk)
            currentChunk = mutableListOf()
        }
        currentChunk.add(item)
    }

    if (currentChunk.isNotEmpty()) {
        result.add(currentChunk)
    }

    return result
}

/**
 * Finds and returns a set of elements that appear more than once in the list.
 *
 * This function iterates through the list and identifies all elements that are duplicates.
 * The result is a `Set` containing each unique duplicate element.
 *
 * @return A [Set] of duplicate elements. If no elements are duplicated, an empty set is returned.
 * @receiver The list to be checked for duplicates.
 */
fun <T> List<T>.duplicates(): List<T> {
    return groupingBy { it }
        .eachCount()
        .filter { it.value > 1 }
        .keys
        .toList()
}