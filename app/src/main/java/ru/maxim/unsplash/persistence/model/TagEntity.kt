package ru.maxim.unsplash.persistence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import ru.maxim.unsplash.persistence.model.PhotoEntity.PhotoContract
import ru.maxim.unsplash.persistence.model.TagEntity.TagContract

@Entity(
    tableName = TagContract.tableName,
    primaryKeys = [TagContract.Columns.photoId, TagContract.Columns.title],
    foreignKeys = [
        ForeignKey(
            entity = PhotoEntity::class,
            parentColumns = [PhotoContract.Columns.id],
            childColumns = [TagContract.Columns.photoId],
            onDelete = CASCADE, onUpdate = NO_ACTION
        )
    ]
)
data class TagEntity(
    @ColumnInfo(name = TagContract.Columns.photoId, index = true)
    var photoId: String,

    @ColumnInfo(name = TagContract.Columns.type)
    val type: String?,

    @ColumnInfo(name = TagContract.Columns.title, index = true)
    val title: String
) {

    object TagContract {
        const val tableName = "tags"

        object Columns {
            const val photoId = "photo_id"
            const val type = "type"
            const val title = "title"
        }
    }
}