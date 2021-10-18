package ru.maxim.unsplash.repository.local.model

import androidx.room.*
import ru.maxim.unsplash.model.Photo
import ru.maxim.unsplash.repository.local.Database
import ru.maxim.unsplash.repository.local.model.DatabaseLocation.Companion.fromLocation
import ru.maxim.unsplash.repository.local.model.DatabasePhoto.Exif.Companion.fromExif
import ru.maxim.unsplash.repository.local.model.DatabasePhoto.Links.Companion.fromLinks
import ru.maxim.unsplash.repository.local.model.DatabasePhoto.PhotoContract
import ru.maxim.unsplash.repository.local.model.DatabasePhoto.Urls.Companion.fromUrls
import ru.maxim.unsplash.repository.local.model.DatabaseUser.Companion.fromUser
import ru.maxim.unsplash.repository.local.model.DatabaseUser.UserContract
import java.util.*

@Entity(
    tableName = PhotoContract.tableName,
    foreignKeys = [
        ForeignKey(
            entity = DatabaseUser::class,
            parentColumns = [UserContract.Columns.id],
            childColumns = [PhotoContract.Columns.userId]
        )
    ]
)
data class DatabasePhoto(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = PhotoContract.Columns.id) val id: String,
    @ColumnInfo(name = PhotoContract.Columns.createdAt) val createdAt: Date,
    @ColumnInfo(name = PhotoContract.Columns.updatedAt) val updatedAt: Date,
    @ColumnInfo(name = PhotoContract.Columns.width) val width: Int,
    @ColumnInfo(name = PhotoContract.Columns.height) val height: Int,
    @ColumnInfo(name = PhotoContract.Columns.color) val color: String?,
    @ColumnInfo(name = PhotoContract.Columns.blurHash) val blurHash: String,
    @ColumnInfo(name = PhotoContract.Columns.likes) var likes: Int,
    @ColumnInfo(name = PhotoContract.Columns.likedByUser) var likedByUser: Boolean,
    @ColumnInfo(name = PhotoContract.Columns.description) val description: String?,
    @ColumnInfo(name = PhotoContract.Columns.userId) var userId: String?,
    @Embedded val location: DatabaseLocation?,
    @Embedded val exif: Exif?,
    @Embedded val urls: Urls,
    @Embedded val links: Links,
) {
    companion object {
        fun Photo.fromPhoto(): DatabasePhoto {
            user?.let {
                Database.instance.userDao().insert(it.fromUser())
            }
            return DatabasePhoto(
                id = id,
                createdAt = createdAt,
                updatedAt = updatedAt,
                width = width,
                height = height,
                color = color,
                blurHash = blurHash,
                likes = likes,
                likedByUser = likedByUser,
                description = description,
                userId = user?.id,
                location = location?.fromLocation(),
                exif = exif?.fromExif(),
                urls = urls.fromUrls(),
                links = links.fromLinks()
            )
        }
    }

    fun toPhoto() = Photo(
        id = id,
        createdAt = createdAt,
        updatedAt = updatedAt,
        width = width,
        height = height,
        color = color,
        blurHash = blurHash,
        likes = likes,
        likedByUser = likedByUser,
        description = description,
        exif = exif?.toExif(),
        location = location?.toLocation(),
        tags = ArrayList(Database.instance.tagDao().getByPhotoId(id).map { it.toTag() }),
        user = userId?.let { Database.instance.userDao().getById(it)?.toUser() },
        urls = urls.toUrls(),
        links = links.toLinks()
    )

    data class Links(
        @ColumnInfo(name = PhotoLinksContract.Columns.self) val self: String,
        @ColumnInfo(name = PhotoLinksContract.Columns.html) val html: String,
        @ColumnInfo(name = PhotoLinksContract.Columns.download) val download: String,
        @ColumnInfo(name = PhotoLinksContract.Columns.downloadLocation) val downloadLocation: String
    ) {
        companion object {
            fun Photo.Links.fromLinks() = Links(self, html, download, downloadLocation)
        }

        fun toLinks() = Photo.Links(self, html, download, downloadLocation)

        object PhotoLinksContract {
            object Columns {
                const val self = "self"
                const val html = "html"
                const val download = "download"
                const val downloadLocation = "download_location"
            }
        }
    }

    data class Urls(
        @ColumnInfo(name = PhotoUrlsContract.Columns.raw) val raw: String,
        @ColumnInfo(name = PhotoUrlsContract.Columns.full) val full: String,
        @ColumnInfo(name = PhotoUrlsContract.Columns.regular) val regular: String,
        @ColumnInfo(name = PhotoUrlsContract.Columns.small) val small: String,
        @ColumnInfo(name = PhotoUrlsContract.Columns.thumb) val thumb: String
    ) {
        companion object {
            fun Photo.Urls.fromUrls() = Urls(raw, full, regular, small, thumb)
        }

        fun toUrls() = Photo.Urls(raw, full, regular, small, thumb)

        object PhotoUrlsContract {
            object Columns {
                const val raw = "raw"
                const val full = "full"
                const val regular = "regular"
                const val small = "small"
                const val thumb = "thumb"
            }
        }
    }

    data class Exif(
        @ColumnInfo(name = PhotoExifContract.Columns.make) val make: String?,
        @ColumnInfo(name = PhotoExifContract.Columns.model) val model: String?,
        @ColumnInfo(name = PhotoExifContract.Columns.exposureTime) val exposureTime: String?,
        @ColumnInfo(name = PhotoExifContract.Columns.aperture) val aperture: String?,
        @ColumnInfo(name = PhotoExifContract.Columns.focalLength) val focalLength: String?,
        @ColumnInfo(name = PhotoExifContract.Columns.iso) val iso: Int?
    ) {

        companion object {
            fun Photo.Exif.fromExif() = Exif(make, model, exposureTime, aperture, focalLength, iso)
        }

        fun toExif() = Photo.Exif(make, model, exposureTime, aperture, focalLength, iso)

        object PhotoExifContract {
            object Columns {
                const val make = "make"
                const val model = "model"
                const val exposureTime = "exposure_time"
                const val aperture = "aperture"
                const val focalLength = "focal_length"
                const val iso = "iso"
            }
        }
    }

    object PhotoContract {
        const val tableName = "photos"

        object Columns {
            const val id = "id"
            const val createdAt = "created_at"
            const val updatedAt = "updated_at"
            const val width = "width"
            const val height = "height"
            const val color = "color"
            const val blurHash = "blur_hash"
            const val likes = "likes"
            const val likedByUser = "liked_by_user"
            const val description = "description"
            const val tags = "tags"
            const val userId = "user_id"
        }
    }
}