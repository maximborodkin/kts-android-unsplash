package ru.maxim.unsplash.network.exception

import retrofit2.HttpException
import retrofit2.Response
/**
* Exception class for represent 403 HTTP code
**/
class ForbiddenException(response: Response<*>) : HttpException(response)