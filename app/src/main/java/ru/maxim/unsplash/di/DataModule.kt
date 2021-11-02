package ru.maxim.unsplash.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.maxim.unsplash.repository.*


val dataModule = module(createdAtStart = true) {

    // Repository
    single<PhotoRepository> {
        PhotoRepositoryImpl(
            photoService = get(),
            photoDao = get(),
            tagDao = get(),
            photoDtoMapper = get(named("photo_dto_mapper")),
            photoEntityMapper = get(named("photo_entity_mapper")),
            tagEntityMapper = get(named("tag_entity_mapper"))
        )
    }

    single<CollectionRepository> {
        CollectionRepositoryImpl(
            collectionService = get(),
            collectionDao = get(),
            collectionDtoMapper = get(named("collection_dto_mapper")),
            collectionEntityMapper = get(named("collection_entity_mapper"))
        )
    }

    single<UserRepository> {
        UserRepositoryImpl(
            userDao = get(),
            userService = get(),
            userEntityMapper = get(named("user_entity_mapper")),
            userDtoMapper = get(named("user_dto_mapper")),
            preferencesManager = get()
        )
    }
}