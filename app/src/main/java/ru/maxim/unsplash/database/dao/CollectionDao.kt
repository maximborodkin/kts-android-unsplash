package ru.maxim.unsplash.database.dao

import androidx.room.*
import ru.maxim.unsplash.database.model.CollectionEntity
import ru.maxim.unsplash.database.model.CollectionEntity.CollectionContract

@Dao
interface CollectionDao {

    @Query("SELECT * FROM ${CollectionContract.tableName}")
    fun getAll(): List<CollectionEntity>

    @Query("SELECT * FROM ${CollectionContract.tableName} WHERE ${CollectionContract.Columns.id}=:id")
    fun getById(id: String): CollectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(collectionEntity: CollectionEntity)

    @Update
    fun update(collectionEntity: CollectionEntity)

    @Delete
    fun delete(collectionEntity: CollectionEntity)
}