package ru.maxim.unsplash.database.mapper

import ru.maxim.unsplash.database.model.TagEntity
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Tag

class TagEntityMapper : DomainMapper<TagEntity, Tag> {

    override suspend fun toDomainModel(model: TagEntity) =
        Tag(
            type = model.type,
            title = model.title
        )

    override suspend fun fromDomainModel(domainModel: Tag, vararg params: String): TagEntity {
        val photoId = params.firstOrNull()
            ?: throw IllegalArgumentException("Photo id is required as second argument")

        return TagEntity(
            photoId = photoId,
            type = domainModel.type,
            title = domainModel.title
        )
    }
}