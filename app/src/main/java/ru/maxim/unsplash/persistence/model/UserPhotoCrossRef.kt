package ru.maxim.unsplash.persistence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import androidx.room.Index
import ru.maxim.unsplash.persistence.model.PhotoEntity.PhotoContract
import ru.maxim.unsplash.persistence.model.UserEntity.UserContract
import ru.maxim.unsplash.persistence.model.UserPhotoCrossRef.UserPhotoContract

@Entity(
    tableName = UserPhotoContract.tableName,
    primaryKeys = [
        UserPhotoContract.Columns.userUsername,
        UserPhotoContract.Columns.photoId
    ],
    indices = [
        Index(
            value = [
                UserPhotoContract.Columns.userUsername,
                UserPhotoContract.Columns.photoId
            ],
            unique = true
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [UserContract.Columns.username],
            childColumns = [UserPhotoContract.Columns.userUsername],
            onDelete = CASCADE, onUpdate = NO_ACTION
        ),
        ForeignKey(
            entity = PhotoEntity::class,
            parentColumns = [PhotoContract.Columns.id],
            childColumns = [UserPhotoContract.Columns.photoId],
            onDelete = CASCADE, onUpdate = NO_ACTION
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