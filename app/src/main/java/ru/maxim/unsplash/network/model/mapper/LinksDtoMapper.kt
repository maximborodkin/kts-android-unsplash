package ru.maxim.unsplash.network.model.mapper

import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Links
import ru.maxim.unsplash.network.model.LinksDto

class LinksDtoMapper : DomainMapper<LinksDto, Links> {

    override fun toDomainModel(model: LinksDto) =
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

    override fun fromDomainModel(domainModel: Links, vararg params: String) =
        LinksDto(
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