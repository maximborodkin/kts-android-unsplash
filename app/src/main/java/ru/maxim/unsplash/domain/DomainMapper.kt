package ru.maxim.unsplash.domain;

interface DomainMapper<T, DomainModel> {

    suspend fun toDomainModel(model: T): DomainModel

    suspend fun fromDomainModel(domainModel: DomainModel, vararg params: String): T

    suspend fun toDomainModelList(model: List<T>): List<DomainModel> = model.map { toDomainModel(it) }

    suspend fun fromDomainModelList(domainModel: List<DomainModel>): List<T> =
        domainModel.map { fromDomainModel(it) }
}
