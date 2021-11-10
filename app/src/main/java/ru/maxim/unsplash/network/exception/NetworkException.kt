package ru.maxim.unsplash.network.exception

import retrofit2.HttpException
import retrofit2.Response

/**
 * Exception class for represent unexpected network exception
 **/
class NetworkException(response: Response<*>) : HttpException(response)