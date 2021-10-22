package ru.maxim.unsplash.database.model

import androidx.room.*
import ru.maxim.unsplash.database.model.CollectionEntity.CollectionContract
import ru.maxim.unsplash.database.model.PhotoEntity.PhotoContract
import ru.maxim.unsplash.database.model.UserEntity.UserContract
import java.util.*

@Entity(
    tableName = CollectionContract.tableName,
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [UserContract.Columns.id],
            childColumns = [CollectionContract.Columns.userId]
        ),
        ForeignKey(
            entity = PhotoEntity::class,
            parentColumns = [PhotoContract.Columns.id],
            childColumns = [CollectionContract.Columns.coverPhotoId]
        )
    ]
)
data class CollectionEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = CollectionContract.Columns.id)
    val id: String,

    @ColumnInfo(name = CollectionContract.Columns.title)
    val title: String,

    @ColumnInfo(name = CollectionContract.Columns.description)
    val description: String?,

    @ColumnInfo(name = CollectionContract.Columns.publishedAt)
    val publishedAt: Date?,

    @ColumnInfo(name = CollectionContract.Columns.updatedAt)
    val updatedAt: Date?,

    @ColumnInfo(name = CollectionContract.Columns.totalPhotos)
    val totalPhotos: Int,

    @ColumnInfo(name = CollectionContract.Columns.isPrivate)
    val isPrivate: Boolean,

    @ColumnInfo(name = CollectionContract.Columns.shareKey)
    val shareKey: String,

    @ColumnInfo(name = CollectionContract.Columns.userId)
    val userId: String?,

    @ColumnInfo(name = CollectionContract.Columns.coverPhotoId)
    val coverPhotoId: String?,

    @Embedded
    val links: LinksEntity
) {
    object CollectionContract {
        const val tableName = "collections"

        object Columns {
            const val id = "id"
            const val title = "title"
            const val description = "description"
            const val publishedAt = "published_at"
            const val updatedAt = "updated_at"
            const val totalPhotos = "total_photos"
            const val isPrivate = "is_private"
            const val shareKey = "share_key"
            const val coverPhotoId = "cover_photo_id"
            const val userId = "user_id"
        }
    }
}
