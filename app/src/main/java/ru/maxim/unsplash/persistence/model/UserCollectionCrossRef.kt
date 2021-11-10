package ru.maxim.unsplash.persistence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import androidx.room.Index
import ru.maxim.unsplash.persistence.model.CollectionEntity.CollectionContract
import ru.maxim.unsplash.persistence.model.UserCollectionCrossRef.UserCollectionContract
import ru.maxim.unsplash.persistence.model.UserEntity.UserContract

@Entity(
    tableName = UserCollectionContract.tableName,
    primaryKeys = [
        UserCollectionContract.Columns.userUsername,
        UserCollectionContract.Columns.collectionId
    ],
    indices = [
        Index(
            value = [
                UserCollectionContract.Columns.userUsername,
                UserCollectionContract.Columns.collectionId
            ],
            unique = true
        )
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [UserContract.Columns.username],
            childColumns = [UserCollectionContract.Columns.userUsername],
            onDelete = CASCADE, onUpdate = NO_ACTION
        ),
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = [CollectionContract.Columns.id],
            childColumns = [UserCollectionContract.Columns.collectionId],
            onDelete = CASCADE, onUpdate = NO_ACTION
        )
    ]
)
data class UserCollectionCrossRef(
    @ColumnInfo(name = UserCollectionContract.Columns.userUsername)
    val userUsername: String,

    @ColumnInfo(name = UserCollectionContract.Columns.collectionId)
    val collectionId: String
) {
    object UserCollectionContract {
        const val tableName = "user_collection"

        object Columns {
            const val userUsername = "user_username"
            const val collectionId = "collection_id"
        }
    }
}