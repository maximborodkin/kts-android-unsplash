package ru.maxim.unsplash.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.maxim.unsplash.repository.local.converter.DateConverter
import ru.maxim.unsplash.repository.local.dao.*
import ru.maxim.unsplash.repository.local.model.*

@Database(
    entities = [
        DatabaseUser::class,
        DatabasePhoto::class,
        DatabaseCollection::class,
        DatabaseTag::class
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