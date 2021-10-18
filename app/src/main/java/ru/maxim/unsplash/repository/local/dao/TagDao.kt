package ru.maxim.unsplash.repository.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.maxim.unsplash.repository.local.model.DatabaseTag
import ru.maxim.unsplash.repository.local.model.DatabaseTag.TagContract

@Dao
interface TagDao {

    @Query("SELECT * FROM ${TagContract.tableName}")
    fun getAll(): List<DatabaseTag>

    @Query("SELECT * FROM ${TagContract.tableName} WHERE ${TagContract.Columns.photoId}=:photoId")
    fun getByPhotoId(photoId: String): List<DatabaseTag>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tag: DatabaseTag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tags: List<DatabaseTag>)
}