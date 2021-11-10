package ru.maxim.unsplash.util

/**
 * Wrapper class for result of repository calls.
 * Contains state of call and its result.
 * [Success] used when call is successful. Contains result data.
 * [Loading] used when call is in progress. Contains data cached data for this call (if exists).
 * [Error] used when call is failed. Contains cached data for this call (if exists) and and
 * exception that was thrown during the request.
 **/
sealed class Result<T> {
    class Success<T>(val data: T) : Result<T>()
    class Loading<T>(val data: T?) : Result<T>()
    class Error<T>(val data: T?, val exception: Exception) : Result<T>()
}
