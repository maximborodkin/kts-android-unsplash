package ru.maxim.unsplash.di

import android.content.Context
import android.net.ConnectivityManager
import net.openid.appauth.AuthorizationService
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.*
import ru.maxim.unsplash.domain.model.Collection
import ru.maxim.unsplash.network.AuthConfig
import ru.maxim.unsplash.network.RetrofitClient
import ru.maxim.unsplash.network.model.*
import ru.maxim.unsplash.network.model.mapper.*
import ru.maxim.unsplash.network.service.AuthService
import ru.maxim.unsplash.util.NetworkUtils


val networkModule = module(createdAtStart = true) {
    // Config singleton
    single { AuthConfig }

    // Network services
    single { RetrofitClient(preferencesManager = get()) }
    single { AuthService(preferencesManager = get()) }
    single { AuthorizationService(androidApplication()) }
    single { get<RetrofitClient>().photoService }
    single { get<RetrofitClient>().collectionService }
    single { get<RetrofitClient>().userService }

    // DTO mappers
    single<DomainMapper<ExifDto, Exif>>(named("exif_dto_mapper")) { ExifDtoMapper() }
    single<DomainMapper<LinksDto, Links>>(named("links_dto_mapper")) { LinksDtoMapper() }
    single<DomainMapper<TagDto, Tag>>(named("tag_dto_mapper")) { TagDtoMapper() }
    single<DomainMapper<UrlsDto, Urls>>(named("urls_dto_mapper")) { UrlsDtoMapper() }
    single<DomainMapper<PositionDto, Position>>(named("position_dto_mapper")) { PositionDtoMapper() }
    single<DomainMapper<LocationDto, Location>>(named("location_dto_mapper")) {
        LocationDtoMapper(positionDtoMapper = get(named("position_dto_mapper")))
    }
    single<DomainMapper<UserDto, User>>(named("user_dto_mapper")) {
        UserDtoMapper(
            urlsDtoMapper = get(named("urls_dto_mapper")),
            linksDtoMapper = get(named("links_dto_mapper"))
        )
    }
    single<DomainMapper<PhotoDto, Photo>>(named("photo_dto_mapper")) {
        PhotoDtoMapper(
            exifDtoMapper = get(named("exif_dto_mapper")),
            locationDtoMapper = get(named("location_dto_mapper")),
            tagDtoMapper = get(named("tag_dto_mapper")),
            userDtoMapper = get(named("user_dto_mapper")),
            urlsDtoMapper = get(named("urls_dto_mapper")),
            linksDtoMapper = get(named("links_dto_mapper"))
        )
    }
    single<DomainMapper<CollectionDto, Collection>>(named("collection_dto_mapper")) {
        CollectionDtoMapper(
            photoDtoMapper = get(named("photo_dto_mapper")),
            userDtoMapper = get(named("user_dto_mapper")),
            linksDtoMapper = get(named("links_dto_mapper"))
        )
    }

    // Utils
    single { androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    single { NetworkUtils(scope = get(), connectivityManager = get()) }
}