package ru.maxim.unsplash.persistence.model

import androidx.room.ColumnInfo

data class UrlsEntity(
    @ColumnInfo(name = UrlsContract.Columns.raw)
    val raw: String?,

    @ColumnInfo(name = UrlsContract.Columns.full)
    val full: String?,

    @ColumnInfo(name = UrlsContract.Columns.regular)
    val regular: String?,

    @ColumnInfo(name = UrlsContract.Columns.small)
    val small: String,

    @ColumnInfo(name = UrlsContract.Columns.thumb)
    val thumb: String?,

    @ColumnInfo(name = UrlsContract.Columns.medium)
    val medium: String?,

    @ColumnInfo(name = UrlsContract.Columns.large)
    val large: String?
) {
    object UrlsContract {
        object Columns {
            const val raw = "raw"
            const val full = "full"
            const val regular = "regular"
            const val small = "small"
            const val thumb = "thumb"
            const val medium = "medium"
            const val large = "large"
        }
    }
}