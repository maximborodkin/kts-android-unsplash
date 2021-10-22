package ru.maxim.unsplash.network.model.mapper

import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Urls
import ru.maxim.unsplash.network.model.UrlsDto

class UrlsDtoMapper : DomainMapper<UrlsDto, Urls> {

    override fun toDomainModel(model: UrlsDto) =
        Urls(
            raw = model.raw,
            full = model.full,
            regular = model.regular,
            small = model.small,
            thumb = model.thumb,
            medium = model.medium,
            large = model.large
        )

    override fun fromDomainModel(domainModel: Urls, vararg params: String) =
        UrlsDto(
            raw = domainModel.raw,
            full = domainModel.full,
            regular = domainModel.regular,
            small = domainModel.small,
            thumb = domainModel.thumb,
            medium = domainModel.medium,
            large = domainModel.large
        )
}