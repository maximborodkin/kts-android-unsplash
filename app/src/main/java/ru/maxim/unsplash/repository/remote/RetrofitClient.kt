package ru.maxim.unsplash.repository.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.maxim.unsplash.repository.remote.service.CollectionService
import ru.maxim.unsplash.repository.remote.service.PhotoService

object RetrofitClient {
    private const val baseUrl = "https://api.unsplash.com/"
//    private const val baseUrl = "https://unsplash.free.beeceptor.com"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = BODY })
        .addInterceptor { chain: Interceptor.Chain ->
            if (AuthConfig.accessToken.isNullOrBlank()) {
                chain.proceed(chain.request())
            } else {
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${AuthConfig.accessToken}")
                    .build()
                chain.proceed(request)
            }
        }
        .build()

    private val instance: Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val photoService: PhotoService by lazy { instance.create(PhotoService::class.java) }
    val collectionService: CollectionService by lazy { instance.create(CollectionService::class.java) }
}