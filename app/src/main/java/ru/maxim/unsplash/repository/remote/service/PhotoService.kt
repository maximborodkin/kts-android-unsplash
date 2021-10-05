package ru.maxim.unsplash.repository.remote.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.maxim.unsplash.model.Photo
import ru.maxim.unsplash.model.response.PhotosSearchResponse
import ru.maxim.unsplash.ui.main.PhotosOrderType
import ru.maxim.unsplash.ui.main.PhotosSearchOrderType

interface PhotoService {

    @GET("/photos")
    suspend fun getAllPaginated(
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = 10,
        @Query("orderBy") orderBy: String = PhotosOrderType.Latest.queryParam
    ): Response<ArrayList<Photo>>

    @GET("/photos/:id")
    suspend fun getById(@Path("id") id: String)

    @GET("/search/photos")
    suspend fun getPaginatedSearch(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = 10,
        @Query("order_by") orderBy: String = PhotosSearchOrderType.Relevant.queryParam
    ): Response<PhotosSearchResponse>
}