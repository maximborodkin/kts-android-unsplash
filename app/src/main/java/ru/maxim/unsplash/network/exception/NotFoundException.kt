package ru.maxim.unsplash.network.exception

import retrofit2.HttpException
import retrofit2.Response
/**
* Exception class for represent 404 HTTP code
**/
class NotFoundException(response: Response<*>) : HttpException(response)