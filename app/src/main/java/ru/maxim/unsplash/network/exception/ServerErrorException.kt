package ru.maxim.unsplash.network.exception

import retrofit2.HttpException
import retrofit2.Response
/**
* Exception class for represent 5xx server error HTTP codes
**/
class ServerErrorException(response: Response<*>) : HttpException(response)