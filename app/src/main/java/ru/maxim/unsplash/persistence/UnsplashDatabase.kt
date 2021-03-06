package ru.maxim.unsplash.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.maxim.unsplash.persistence.converter.DateConverter
import ru.maxim.unsplash.persistence.dao.CollectionDao
import ru.maxim.unsplash.persistence.dao.PhotoDao
import ru.maxim.unsplash.persistence.dao.TagDao
import ru.maxim.unsplash.persistence.dao.UserDao
import ru.maxim.unsplash.persistence.model.*

@Database(
    entities = [
        CollectionEntity::class,
        PhotoEntity::class,
        TagEntity::class,
        UserEntity::class,
        CollectionPhotoCrossRef::class,
        UserPhotoCrossRef::class,
        UserCollectionCrossRef::class,
        UserLikeCrossRef::class
    ],
    version = UnsplashDatabase.databaseVersion
)
@TypeConverters(DateConverter::class)
abstract class UnsplashDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun photoDao(): PhotoDao
    abstract fun tagDao(): TagDao
    abstract fun collectionDao(): CollectionDao

    companion object {
        const val databaseVersion = 1
        const val databaseName = "database"
    }
}