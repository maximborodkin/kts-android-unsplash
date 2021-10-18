package ru.maxim.unsplash.repository.local.dao

import androidx.room.*
import ru.maxim.unsplash.repository.local.model.DatabaseCollection
import ru.maxim.unsplash.repository.local.model.DatabaseCollection.CollectionContract

@Dao
interface CollectionDao {

    @Query("SELECT * FROM ${CollectionContract.tableName}")
    fun getAll(): List<DatabaseCollection>

    @Query("SELECT * FROM ${CollectionContract.tableName} WHERE ${CollectionContract.Columns.id}=:id")
    fun getById(id: String): DatabaseCollection?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(collection: DatabaseCollection)

    @Update
    fun update(collection: DatabaseCollection)

    @Delete
    fun delete(collection: DatabaseCollection)
}