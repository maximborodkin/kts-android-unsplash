package ru.maxim.unsplash.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.maxim.unsplash.database.model.CollectionEntity
import ru.maxim.unsplash.database.model.CollectionEntity.CollectionContract

@Dao
interface CollectionDao {

    @Query("SELECT * FROM ${CollectionContract.tableName} ORDER BY ${CollectionContract.Columns.cacheTime}")
    fun getAll(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM ${CollectionContract.tableName} WHERE ${CollectionContract.Columns.id}=:id")
    fun getById(id: String): Flow<CollectionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(collectionEntity: CollectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(collectionEntities: List<CollectionEntity>)

    @Update
    fun update(collectionEntity: CollectionEntity)

    @Delete()
    fun delete(collectionEntity: CollectionEntity)

    @Query("DELETE FROM ${CollectionContract.tableName}")
    fun deleteAll()
}