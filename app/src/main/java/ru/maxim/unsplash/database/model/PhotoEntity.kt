package ru.maxim.unsplash.database.model

import androidx.room.*
import ru.maxim.unsplash.database.model.PhotoEntity.PhotoContract
import ru.maxim.unsplash.database.model.UserEntity.UserContract
import java.util.*

@Entity(
    tableName = PhotoContract.tableName,
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [UserContract.Columns.id],
            childColumns = [PhotoContract.Columns.userId]
        )
    ]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = PhotoContract.Columns.id)
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
    val blurHash: String,

    @ColumnInfo(name = PhotoContract.Columns.likes)
    var likes: Int,

    @ColumnInfo(name = PhotoContract.Columns.likedByUser)
    var likedByUser: Boolean,

    @ColumnInfo(name = PhotoContract.Columns.description)
    val description: String?,

    @ColumnInfo(name = PhotoContract.Columns.userId)
    var userId: String?,

    @Embedded
    val location: LocationEntity?,

    @Embedded
    val exif: ExifEntity?,

    @Embedded
    val urls: UrlsEntity,

    @Embedded(prefix = "links")
    val links: LinksEntity,
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
            const val userId = "user_id"
        }
    }
}