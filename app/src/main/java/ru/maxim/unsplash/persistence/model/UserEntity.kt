package ru.maxim.unsplash.persistence.model


import androidx.room.*
import ru.maxim.unsplash.persistence.model.UserEntity.UserContract

@Entity(
    tableName = UserContract.tableName,
    indices = [Index(value = [UserContract.Columns.username], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = UserContract.Columns.id, index = true)
    val id: String,

    @ColumnInfo(name = UserContract.Columns.username)
    val username: String,

    @ColumnInfo(name = UserContract.Columns.name)
    val name: String,

    @ColumnInfo(name = UserContract.Columns.portfolioUrl)
    val portfolioUrl: String?,

    @ColumnInfo(name = UserContract.Columns.bio)
    val bio: String?,

    @ColumnInfo(name = UserContract.Columns.location)
    val location: String?,

    @ColumnInfo(name = UserContract.Columns.totalLikes)
    val totalLikes: Int,

    @ColumnInfo(name = UserContract.Columns.totalPhotos)
    val totalPhotos: Int,

    @ColumnInfo(name = UserContract.Columns.totalCollections)
    val totalCollections: Int,

    @Embedded
    val profileImage: UrlsEntity,

    @Embedded
    val links: LinksEntity,

    //Used for ordering cached items to retain fetched items order
    @ColumnInfo(name = UserContract.Columns.cacheTime)
    val cacheTime: Long
) {
    object UserContract {
        const val tableName = "users"

        object Columns {
            const val id = "id"
            const val username = "username"
            const val name = "name"
            const val portfolioUrl = "portfolio_url"
            const val bio = "bio"
            const val location = "location"
            const val totalLikes = "total_likes"
            const val totalPhotos = "total_photos"
            const val totalCollections = "total_collections"
            const val profileImage = "profile_image"
            const val links = "links"
            const val cacheTime = "cache_time"
        }
    }
}