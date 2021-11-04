package ru.maxim.unsplash.util

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import retrofit2.Response
import ru.maxim.unsplash.network.exception.*
import timber.log.Timber
import java.io.IOException

fun <DomainType, ResponseType> networkBoundResource(
    query: suspend () -> Flow<DomainType>,
    fetch: suspend () -> Response<ResponseType>,
    cacheFetchResult: suspend (ResponseType) -> Unit,
    shouldFetch: (DomainType) -> Boolean = { true }
) = flow {

    // Database result flows may throw [IllegalStateException] when a query fails.
    // In this case, the flow collector must handle these exceptions and pass this inconsistent data.
    suspend fun queryFlow() = query().flowOn(IO).catch {  }

    val cache = queryFlow().first()

    val responseFlow = if (shouldFetch(cache)) {
        emit(Result.Loading(cache))

        try {
            val response = withContext(IO) { fetch() }
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                withContext(IO) { cacheFetchResult(responseBody) }
                queryFlow().map { Result.Success(it) }
            } else {
                throw when (response.code()) {
                    401 -> UnauthorizedException(response)
                    403 -> ForbiddenException(response)
                    404 -> NotFoundException(response)
                    408 -> TimeoutException(response)
                    in 500..599 -> ServerErrorException(response)
                    else -> NetworkException(response)
                }
            }
        } catch (e: Exception) {
            Timber.w(e)
            val exception = if (e is IOException) NoConnectionException() else e
            queryFlow().map { Result.Error(it, exception) }
        }
    } else {
        queryFlow().map { Result.Success(it) }
    }

    emitAll(responseFlow)
}