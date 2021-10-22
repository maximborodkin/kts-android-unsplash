package ru.maxim.unsplash.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.maxim.unsplash.database.model.TagEntity
import ru.maxim.unsplash.database.model.TagEntity.TagContract

@Dao
interface TagDao {

    @Query("SELECT * FROM ${TagContract.tableName}")
    fun getAll(): List<TagEntity>

    @Query("SELECT * FROM ${TagContract.tableName} WHERE ${TagContract.Columns.photoId}=:photoId")
    fun getByPhotoId(photoId: String): List<TagEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tagEntity: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tagEntities: List<TagEntity>)
}