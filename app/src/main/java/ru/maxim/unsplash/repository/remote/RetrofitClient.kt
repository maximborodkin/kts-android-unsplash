package ru.maxim.unsplash.repository.remote

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.maxim.unsplash.repository.local.PreferencesManager
import ru.maxim.unsplash.repository.remote.service.CollectionService
import ru.maxim.unsplash.repository.remote.service.PhotoService

object RetrofitClient {
    private const val baseUrl = "https://api.unsplash.com/"
//    private const val baseUrl = "https://unsplash.free.beeceptor.com"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = BODY })
        .addInterceptor { chain: Interceptor.Chain ->
            val token = PreferencesManager.accessToken
            if (token.isNullOrBlank()) {
                chain.proceed(chain.request())
            } else {
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
        }
        .build()

    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create()
    private val instance: Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val photoService: PhotoService by lazy { instance.create(PhotoService::class.java) }
    val collectionService: CollectionService by lazy { instance.create(CollectionService::class.java) }
}