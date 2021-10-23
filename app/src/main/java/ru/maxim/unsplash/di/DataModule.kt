package ru.maxim.unsplash.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.maxim.unsplash.repository.CollectionRepository
import ru.maxim.unsplash.repository.CollectionRepositoryImpl
import ru.maxim.unsplash.repository.PhotoRepository
import ru.maxim.unsplash.repository.PhotoRepositoryImpl


val dataModule = module(createdAtStart = true) {

    // Repository
    single<PhotoRepository> {
        PhotoRepositoryImpl(
            photoService = get(),
            photoDao = get(),
            tagDao = get(),
            photoDtoMapper = get(named("photo_dto_mapper")),
            tagDtoMapper = get(named("tag_dto_mapper")),
            photoEntityMapper = get(named("photo_entity_mapper")),
            tagEntityMapper = get(named("tag_entity_mapper"))
        )
    }

    single<CollectionRepository> {
        CollectionRepositoryImpl(
            collectionService = get(),
            photoRepository = get(),
            collectionDao = get(),
            collectionDtoMapper = get(named("collection_dto_mapper")),
            collectionEntityMapper = get(named("collection_entity_mapper"))
        )
    }
}