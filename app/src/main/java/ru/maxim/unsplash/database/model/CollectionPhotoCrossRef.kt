package ru.maxim.unsplash.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import ru.maxim.unsplash.database.model.CollectionEntity.CollectionContract
import ru.maxim.unsplash.database.model.CollectionPhotoCrossRef.CollectionPhotoContract
import ru.maxim.unsplash.database.model.PhotoEntity.PhotoContract

@Entity(
    tableName = CollectionPhotoContract.tableName,
    primaryKeys = [
        CollectionPhotoContract.Columns.collectionId,
        CollectionPhotoContract.Columns.photoId
    ],
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = [CollectionContract.Columns.id],
            childColumns = [CollectionPhotoContract.Columns.collectionId],
            onDelete = CASCADE, onUpdate = NO_ACTION
        ),
        ForeignKey(
            entity = PhotoEntity::class,
            parentColumns = [PhotoContract.Columns.id],
            childColumns = [CollectionPhotoContract.Columns.photoId],
            onDelete = CASCADE, onUpdate = NO_ACTION
        )
    ]
)
data class CollectionPhotoCrossRef(
    @ColumnInfo(name = CollectionPhotoContract.Columns.collectionId)
    val collectionId: String,

    @ColumnInfo(name = CollectionPhotoContract.Columns.photoId)
    val photoId: String
) {
    object CollectionPhotoContract {
        const val tableName = "collection_photo"

        object Columns {
            const val collectionId = "collection_id"
            const val photoId = "photo_id"
        }
    }
}