package ru.maxim.unsplash.repository.local.model

import androidx.room.*
import ru.maxim.unsplash.model.Collection
import ru.maxim.unsplash.repository.local.Database
import ru.maxim.unsplash.repository.local.model.DatabaseCollection.CollectionContract
import ru.maxim.unsplash.repository.local.model.DatabaseCollection.Links.Companion.fromLinks
import ru.maxim.unsplash.repository.local.model.DatabasePhoto.Companion.fromPhoto
import ru.maxim.unsplash.repository.local.model.DatabasePhoto.PhotoContract
import ru.maxim.unsplash.repository.local.model.DatabaseUser.Companion.fromUser
import ru.maxim.unsplash.repository.local.model.DatabaseUser.UserContract
import java.util.*

@Entity(tableName = CollectionContract.tableName,
    foreignKeys = [
        ForeignKey(
            entity = DatabaseUser::class,
            parentColumns = [UserContract.Columns.id],
            childColumns = [CollectionContract.Columns.userId]
        ),
        ForeignKey(
            entity = DatabasePhoto::class,
            parentColumns = [PhotoContract.Columns.id],
            childColumns = [CollectionContract.Columns.coverPhotoId]
        )
    ])
data class DatabaseCollection(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = CollectionContract.Columns.id) val id: String,
    @ColumnInfo(name = CollectionContract.Columns.title) val title: String,
    @ColumnInfo(name = CollectionContract.Columns.description) val description: String?,
    @ColumnInfo(name = CollectionContract.Columns.createdAt) val createdAt: Date?,
    @ColumnInfo(name = CollectionContract.Columns.updatedAt) val updatedAt: Date?,
    @ColumnInfo(name = CollectionContract.Columns.coverPhotoId) val coverPhotoId: String?,
    @ColumnInfo(name = CollectionContract.Columns.totalPhotos) val totalPhotos: Int,
    @ColumnInfo(name = CollectionContract.Columns.userId) val userId: String?,
    @Embedded val links: Links
) {
    companion object {
        fun Collection.fromCollection(): DatabaseCollection {
            user?.let {
                Database.instance.userDao().insert(it.fromUser())
            }
            coverPhoto?.let {
                Database.instance.photoDao().insert(it.fromPhoto())
            }
            return DatabaseCollection(
                id = id,
                title = title,
                description = description,
                createdAt = createdAt,
                updatedAt = updatedAt,
                coverPhotoId = coverPhoto?.id,
                totalPhotos = totalPhotos,
                userId = user?.id,
                links = links.fromLinks()
            )
        }
    }

    fun toCollection(): Collection =
        Collection(id,
            title,
            description,
            createdAt,
            updatedAt,
            coverPhotoId?.let { Database.instance.photoDao().getById(it)?.toPhoto() },
            totalPhotos,
            userId?.let { Database.instance.userDao().getById(userId)?.toUser() },
            links.toLinks()
        )

    data class Links (
        @ColumnInfo(name = CollectionLinksContract.Columns.self) val self: String,
        @ColumnInfo(name = CollectionLinksContract.Columns.html) val html: String,
        @ColumnInfo(name = CollectionLinksContract.Columns.photos) val photos: String,
        @ColumnInfo(name = CollectionLinksContract.Columns.related) val related: String
    ) {
        companion object {
            fun Collection.Links.fromLinks(): Links = Links(self, html, photos, related)
        }

        fun toLinks(): Collection.Links = Collection.Links(self, html, photos, related)

        object CollectionLinksContract {
            object Columns {
                const val self = "self"
                const val html = "html"
                const val photos = "photos"
                const val related = "related"
            }
        }
    }

    object CollectionContract {
        const val tableName = "collections"
        object Columns {
            const val id = "id"
            const val title = "title"
            const val description = "description"
            const val createdAt = "created_at"
            const val updatedAt = "updated_at"
            const val coverPhotoId = "cover_photo_id"
            const val totalPhotos = "total_photos"
            const val userId = "user_id"
            const val links = "links"
        }
    }
}
