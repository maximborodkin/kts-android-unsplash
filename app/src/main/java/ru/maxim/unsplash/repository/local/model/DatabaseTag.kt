package ru.maxim.unsplash.repository.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import ru.maxim.unsplash.model.Tag
import ru.maxim.unsplash.repository.local.model.DatabasePhoto.PhotoContract
import ru.maxim.unsplash.repository.local.model.DatabaseTag.TagContract

@Entity(
    tableName = TagContract.tableName,
    primaryKeys = [TagContract.Columns.photoId, TagContract.Columns.title],
    foreignKeys = [
        ForeignKey(
            entity = DatabasePhoto::class,
            parentColumns = [PhotoContract.Columns.id],
            childColumns = [TagContract.Columns.photoId]
        )
    ]
)
data class DatabaseTag (
    @ColumnInfo(name = TagContract.Columns.photoId) var photoId: String,
    @ColumnInfo(name = TagContract.Columns.title) val title: String
) {
    companion object {
        fun Tag.fromTag(photoId: String) = DatabaseTag(photoId, title)
    }

    fun toTag() = Tag(title)

    object TagContract {
        const val tableName = "tags"
        object Columns {
            const val photoId = "photo_id"
            const val title = "title"
        }
    }
}