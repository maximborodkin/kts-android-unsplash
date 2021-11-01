package ru.maxim.unsplash.persistence.mapper

import ru.maxim.unsplash.persistence.model.LinksEntity
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Links

class LinksEntityMapper : DomainMapper<LinksEntity, Links> {

    override suspend fun toDomainModel(model: LinksEntity) =
        Links(
            self = model.self,
            html = model.html,
            photos = model.photos,
            related = model.related,
            download = model.download,
            downloadLocation = model.downloadLocation,
            likes = model.likes,
            portfolio = model.portfolio
        )

    override suspend fun fromDomainModel(domainModel: Links, vararg params: String) =
        LinksEntity(
            self = domainModel.self,
            html = domainModel.html,
            photos = domainModel.photos,
            related = domainModel.related,
            download = domainModel.download,
            downloadLocation = domainModel.downloadLocation,
            likes = domainModel.likes,
            portfolio = domainModel.portfolio
        )
}