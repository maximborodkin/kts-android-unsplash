package ru.maxim.unsplash.persistence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.*
import ru.maxim.unsplash.persistence.model.PhotoEntity.PhotoContract
import ru.maxim.unsplash.persistence.model.UserEntity.UserContract
import ru.maxim.unsplash.persistence.model.UserPhotoCrossRef.UserPhotoContract

@Entity(
    tableName = UserPhotoContract.tableName,
    primaryKeys = [
        UserPhotoContract.Columns.userUsername,
        UserPhotoContract.Columns.photoId
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [UserContract.Columns.id],
            childColumns = [UserPhotoContract.Columns.userUsername],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = PhotoEntity::class,
            parentColumns = [PhotoContract.Columns.id],
            childColumns = [UserPhotoContract.Columns.photoId],
            onDelete = CASCADE
        )
    ]
)
data class UserPhotoCrossRef(
    @ColumnInfo(name = UserPhotoContract.Columns.userUsername)
    val userUsername: String,

    @ColumnInfo(name = UserPhotoContract.Columns.photoId)
    val photoId: String
) {
    object UserPhotoContract {
        const val tableName = "user_photo"

        object Columns {
            const val userUsername = "user_username"
            const val photoId = "photo_id"
        }
    }
}