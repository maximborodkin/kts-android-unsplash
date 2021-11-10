package ru.maxim.unsplash.network.model.mapper

import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Tag
import ru.maxim.unsplash.network.model.TagDto

class TagDtoMapper : DomainMapper<TagDto, Tag> {

    override suspend fun toDomainModel(model: TagDto) =
        Tag(
            type = model.type,
            title = model.title
        )

    override suspend fun fromDomainModel(domainModel: Tag, vararg params: String) =
        TagDto(
            type = domainModel.type,
            title = domainModel.title
        )
}