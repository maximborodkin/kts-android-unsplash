package ru.maxim.unsplash.repository.remote

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.maxim.unsplash.repository.remote.service.PhotoService

object RetrofitClient {
    private const val baseUrl = "https://api.unsplash.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = BASIC })
        .addInterceptor { chain: Interceptor.Chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${AuthConfig.accessToken}")
                .build()
            chain.proceed(request)
        }
        .build()

    val instance = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val photoService by lazy { instance.create(PhotoService::class.java) }
}