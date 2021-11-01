package ru.maxim.unsplash.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.maxim.unsplash.persistence.converter.DateConverter
import ru.maxim.unsplash.persistence.dao.*
import ru.maxim.unsplash.persistence.model.*

@Database(
    entities = [
        CollectionEntity::class,
        PhotoEntity::class,
        TagEntity::class,
        UserEntity::class,
        CollectionPhotoCrossRef::class
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