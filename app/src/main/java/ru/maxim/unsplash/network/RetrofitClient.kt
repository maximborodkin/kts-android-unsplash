package ru.maxim.unsplash.network

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.maxim.unsplash.network.service.CollectionService
import ru.maxim.unsplash.network.service.PhotoService
import ru.maxim.unsplash.network.service.UserService
import ru.maxim.unsplash.persistence.PreferencesManager

class RetrofitClient(private val preferencesManager: PreferencesManager) {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = BODY })
        .addInterceptor { chain: Interceptor.Chain ->
            val token = preferencesManager.accessToken
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

    private val instance: Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(baseUrl)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create()
            )
        )
        .build()

    val photoService: PhotoService by lazy { instance.create(PhotoService::class.java) }
    val collectionService: CollectionService by lazy { instance.create(CollectionService::class.java) }
    val userService: UserService by lazy { instance.create(UserService::class.java) }

    companion object {
        private const val baseUrl = "https://api.unsplash.com/"
    }
}