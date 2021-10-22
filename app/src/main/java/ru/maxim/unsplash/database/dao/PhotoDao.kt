package ru.maxim.unsplash.database.dao

import androidx.room.*
import ru.maxim.unsplash.database.model.PhotoEntity
import ru.maxim.unsplash.database.model.PhotoEntity.PhotoContract

@Dao
interface PhotoDao {

    @Query("SELECT * FROM ${PhotoContract.tableName}")
    fun getAll(): List<PhotoEntity>

    @Query("SELECT * FROM ${PhotoContract.tableName} WHERE ${PhotoContract.Columns.id}=:id")
    fun getById(id: String): PhotoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photoEntity: PhotoEntity)

    @Update
    fun update(photoEntity: PhotoEntity)

    @Delete
    fun delete(photoEntity: PhotoEntity)
}