package ru.maxim.unsplash.network.exception

import retrofit2.HttpException
import retrofit2.Response

/**
 * Exception class for represent 401 HTTP code
 **/
class UnauthorizedException(response: Response<*>) : HttpException(response)