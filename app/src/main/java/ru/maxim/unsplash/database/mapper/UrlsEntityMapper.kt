package ru.maxim.unsplash.database.mapper

import ru.maxim.unsplash.database.model.UrlsEntity
import ru.maxim.unsplash.domain.DomainMapper
import ru.maxim.unsplash.domain.model.Urls

class UrlsEntityMapper : DomainMapper<UrlsEntity, Urls> {

    override fun toDomainModel(model: UrlsEntity) =
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
        UrlsEntity(
            raw = domainModel.raw,
            full = domainModel.full,
            regular = domainModel.regular,
            small = domainModel.small,
            thumb = domainModel.thumb,
            medium = domainModel.medium,
            large = domainModel.large
        )
}