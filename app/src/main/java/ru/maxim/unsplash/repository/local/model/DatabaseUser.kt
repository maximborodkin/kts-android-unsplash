package ru.maxim.unsplash.repository.local.model


import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.maxim.unsplash.model.User
import ru.maxim.unsplash.repository.local.model.DatabaseUser.Links.Companion.fromLinks
import ru.maxim.unsplash.repository.local.model.DatabaseUser.ProfileImage.Companion.fromProfileImage
import ru.maxim.unsplash.repository.local.model.DatabaseUser.UserContract

@Entity(tableName = UserContract.tableName)
data class DatabaseUser(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = UserContract.Columns.id) val id: String,
    @ColumnInfo(name = UserContract.Columns.username) val username: String,
    @ColumnInfo(name = UserContract.Columns.name) val name: String,
    @ColumnInfo(name = UserContract.Columns.portfolioUrl) val portfolioUrl: String?,
    @ColumnInfo(name = UserContract.Columns.bio) val bio: String?,
    @ColumnInfo(name = UserContract.Columns.location) val location: String?,
    @ColumnInfo(name = UserContract.Columns.totalLikes) val totalLikes: Int,
    @ColumnInfo(name = UserContract.Columns.totalPhotos) val totalPhotos: Int,
    @ColumnInfo(name = UserContract.Columns.totalCollections) val totalCollections: Int,
    @Embedded val profileImage: ProfileImage,
    @Embedded val links: Links
) {
    companion object {
        fun User.fromUser(): DatabaseUser {
            return DatabaseUser(
                id = id,
                username = username,
                name = name,
                portfolioUrl = portfolioUrl,
                bio = bio,
                location = location,
                totalLikes = totalLikes,
                totalPhotos = totalPhotos,
                totalCollections = totalCollections,
                profileImage = profileImage.fromProfileImage(),
                links = links.fromLinks()
            )
        }
    }

    fun toUser(): User = User(
        id,
        username,
        name,
        portfolioUrl,
        bio,
        location,
        totalLikes,
        totalPhotos,
        totalCollections,
        profileImage.toProfileImage(),
        links.toLinks()
    )

    data class Links(
        @ColumnInfo(name = UserLinksContract.Columns.self) val self: String,
        @ColumnInfo(name = UserLinksContract.Columns.html) val html: String,
        @ColumnInfo(name = UserLinksContract.Columns.photos) val photos: String,
        @ColumnInfo(name = UserLinksContract.Columns.likes) val likes: String,
        @ColumnInfo(name = UserLinksContract.Columns.portfolio) val portfolio: String
    ) {
        companion object {
            fun User.Links.fromLinks() = Links(self, html, photos, likes, portfolio)
        }

        fun toLinks(): User.Links = User.Links(self, html, photos, likes, portfolio)

        object UserLinksContract {
            object Columns {
                const val self = "self"
                const val html = "html"
                const val photos = "photos"
                const val likes = "likes"
                const val portfolio = "portfolio"
            }
        }

    }

    data class ProfileImage(
        @ColumnInfo(name = ProfileImageContract.Columns.small) val small: String,
        @ColumnInfo(name = ProfileImageContract.Columns.medium) val medium: String,
        @ColumnInfo(name = ProfileImageContract.Columns.large) val large: String
    ) {
        companion object {
            fun User.ProfileImage.fromProfileImage() = ProfileImage(small, medium, large)
        }

        fun toProfileImage(): User.ProfileImage = User.ProfileImage(small, medium, large)

        object ProfileImageContract {
            object Columns {
                const val small = "small"
                const val medium = "medium"
                const val large = "large"
            }
        }
    }

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
        }
    }
}