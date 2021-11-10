package ru.maxim.unsplash.network.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import ru.maxim.unsplash.network.model.UserDto

interface UserService {

    @GET("users/{username}")
    suspend fun getByUsername(@Path("username") username: String): Response<UserDto>

    @GET("/me")
    suspend fun getCurrentUser(): Response<UserDto>
}