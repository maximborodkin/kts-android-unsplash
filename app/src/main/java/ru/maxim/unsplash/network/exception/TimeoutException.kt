package ru.maxim.unsplash.network.exception

import retrofit2.HttpException
import retrofit2.Response

/**
 * Exception class for represent 408 HTTP code
 **/
class TimeoutException(response: Response<*>) : HttpException(response)