package ru.maxim.unsplash.persistence.model

import androidx.room.ColumnInfo

data class LinksEntity(
    @ColumnInfo(name = LinksContract.Columns.self)
    val self: String,

    @ColumnInfo(name = LinksContract.Columns.html)
    val html: String,

    @ColumnInfo(name = LinksContract.Columns.photos)
    val photos: String?,

    @ColumnInfo(name = LinksContract.Columns.related)
    val related: String?,

    @ColumnInfo(name = LinksContract.Columns.download)
    val download: String?,

    @ColumnInfo(name = LinksContract.Columns.downloadLocation)
    val downloadLocation: String?,

    @ColumnInfo(name = LinksContract.Columns.likes)
    val likes: String?,

    @ColumnInfo(name = LinksContract.Columns.portfolio)
    val portfolio: String?
) {
    object LinksContract {
        object Columns {
            const val self = "self"
            const val html = "html"
            const val photos = "photos"
            const val related = "related"
            const val download = "download"
            const val downloadLocation = "download_location"
            const val likes = "likes"
            const val portfolio = "portfolio"
        }
    }
}