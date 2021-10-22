package ru.maxim.unsplash.domain;

interface DomainMapper<T, DomainModel> {

    fun toDomainModel(model: T): DomainModel

    fun fromDomainModel(domainModel: DomainModel, vararg params: String): T

    fun toDomainModelList(model: List<T>): List<DomainModel> = model.map { toDomainModel(it) }

    fun fromDomainModelList(domainModel: List<DomainModel>): List<T> =
        domainModel.map { fromDomainModel(it) }
}
