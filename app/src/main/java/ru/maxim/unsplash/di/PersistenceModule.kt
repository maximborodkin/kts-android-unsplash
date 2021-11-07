package ru.maxim.unsplash.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.*
import ru.maxim.unsplash.domain.model.Collection
import ru.maxim.unsplash.persistence.ExternalStorageManager
import ru.maxim.unsplash.persistence.PreferencesManager
import ru.maxim.unsplash.persistence.UnsplashDatabase
import ru.maxim.unsplash.persistence.mapper.*
import ru.maxim.unsplash.persistence.model.*

val persistenceModule = module(createdAtStart = true) {

    // Preferences manager
    single { PreferencesManager(androidContext()) }

    single { ExternalStorageManager(androidContext()) }

    // Database instance
    single {
        Room.databaseBuilder(
            androidContext(),
            UnsplashDatabase::class.java,
            UnsplashDatabase.databaseName
        ).build()
    }

    // Database DAO
    single { get<UnsplashDatabase>().photoDao() }
    single { get<UnsplashDatabase>().collectionDao() }
    single { get<UnsplashDatabase>().userDao() }
    single { get<UnsplashDatabase>().tagDao() }

    // Entity mappers
    single<DomainMapper<ExifEntity, Exif>>(named("exif_entity_mapper")) { ExifEntityMapper() }
    single<DomainMapper<LinksEntity, Links>>(named("links_entity_mapper")) { LinksEntityMapper() }
    single<DomainMapper<TagEntity, Tag>>(named("tag_entity_mapper")) { TagEntityMapper() }
    single<DomainMapper<UrlsEntity, Urls>>(named("urls_entity_mapper")) { UrlsEntityMapper() }
    single<DomainMapper<PositionEntity, Position>>(named("position_entity_mapper")) {
        PositionEntityMapper()
    }
    single<DomainMapper<LocationEntity, Location>>(named("location_entity_mapper")) {
        LocationEntityMapper(positionEntityMapper = get(named("position_entity_mapper")))
    }
    single<DomainMapper<UserEntity, User>>(named("user_entity_mapper")) {
        UserEntityMapper(
            urlsEntityMapper = get(named("urls_entity_mapper")),
            linksEntityMapper = get(named("links_entity_mapper"))
        )
    }
    single<DomainMapper<PhotoEntity, Photo>>(named("photo_entity_mapper")) {
        PhotoEntityMapper(
            tagDao = get(),
            userDao = get(),
            locationEntityMapper = get(named("location_entity_mapper")),
            exifEntityMapper = get(named("exif_entity_mapper")),
            tagEntityMapper = get(named("tag_entity_mapper")),
            userEntityMapper = get(named("user_entity_mapper")),
            urlsEntityMapper = get(named("urls_entity_mapper")),
            linksEntityMapper = get(named("links_entity_mapper"))
        )
    }
    single<DomainMapper<CollectionEntity, Collection>>(named("collection_entity_mapper")) {
        CollectionEntityMapper(
            userEntityMapper = get(named("user_entity_mapper")),
            photoEntityMapper = get(named("photo_entity_mapper")),
            linksEntityMapper = get(named("links_entity_mapper")),
            userDao = get(),
            photoDao = get()
        )
    }
}