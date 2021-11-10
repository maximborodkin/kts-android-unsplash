package ru.maxim.unsplash.persistence.model

import androidx.room.*
import androidx.room.ForeignKey.*
import ru.maxim.unsplash.persistence.model.PhotoEntity.PhotoContract
import ru.maxim.unsplash.persistence.model.UserEntity.UserContract
import java.util.Date

@Entity(
    tableName = PhotoContract.tableName,
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [UserContract.Columns.username],
            childColumns = [PhotoContract.Columns.userUsername],
            onDelete = CASCADE, onUpdate = NO_ACTION
        )
    ]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = PhotoContract.Columns.id, index = true)
    val id: String,

    @ColumnInfo(name = PhotoContract.Columns.createdAt)
    val createdAt: Date,

    @ColumnInfo(name = PhotoContract.Columns.updatedAt)
    val updatedAt: Date,

    @ColumnInfo(name = PhotoContract.Columns.width)
    val width: Int,

    @ColumnInfo(name = PhotoContract.Columns.height)
    val height: Int,

    @ColumnInfo(name = PhotoContract.Columns.color)
    val color: String?,

    @ColumnInfo(name = PhotoContract.Columns.blurHash)
    val blurHash: String?,

    @ColumnInfo(name = PhotoContract.Columns.likes)
    var likes: Int,

    @ColumnInfo(name = PhotoContract.Columns.likedByUser)
    var likedByUser: Boolean,

    @ColumnInfo(name = PhotoContract.Columns.description)
    val description: String?,

    @ColumnInfo(name = PhotoContract.Columns.userUsername, index = true)
    var userUsername: String,

    @Embedded
    val location: LocationEntity?,

    @Embedded
    val exif: ExifEntity?,

    @Embedded
    val urls: UrlsEntity,

    @Embedded(prefix = "links")
    val links: LinksEntity,

    //Used for ordering cached items to retain fetched items order
    @ColumnInfo(name = PhotoContract.Columns.cacheTime)
    val cacheTime: Long
) {
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
            const val userUsername = "user_username"
            const val cacheTime = "cache_time"
        }
    }
}