package ru.maxim.unsplash.persistence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import androidx.room.Index
import ru.maxim.unsplash.persistence.model.PhotoEntity.PhotoContract
import ru.maxim.unsplash.persistence.model.UserEntity.UserContract
import ru.maxim.unsplash.persistence.model.UserLikeCrossRef.UserLikeContract

@Entity(
    tableName = UserLikeContract.tableName,
    primaryKeys = [
        UserLikeContract.Columns.userUsername,
        UserLikeContract.Columns.photoId
    ],
    indices = [
        Index(
            value = [
                UserLikeContract.Columns.userUsername,
                UserLikeContract.Columns.photoId
            ],
            unique = true
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [UserContract.Columns.username],
            childColumns = [UserLikeContract.Columns.userUsername],
            onDelete = CASCADE, onUpdate = NO_ACTION
        ),
        ForeignKey(
            entity = PhotoEntity::class,
            parentColumns = [PhotoContract.Columns.id],
            childColumns = [UserLikeContract.Columns.photoId],
            onDelete = CASCADE, onUpdate = NO_ACTION
        )
    ]
)
data class UserLikeCrossRef(
    @ColumnInfo(name = UserLikeContract.Columns.userUsername)
    val userUsername: String,

    @ColumnInfo(name = UserLikeContract.Columns.photoId)
    val photoId: String
) {
    object UserLikeContract {
        const val tableName = "user_liked_photo"

        object Columns {
            const val userUsername = "user_username"
            const val photoId = "photo_id"
        }
    }
}