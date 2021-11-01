package ru.maxim.unsplash.persistence.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.maxim.unsplash.persistence.model.CollectionPhotoCrossRef
import ru.maxim.unsplash.persistence.model.CollectionPhotoCrossRef.CollectionPhotoContract
import ru.maxim.unsplash.persistence.model.PhotoEntity
import ru.maxim.unsplash.persistence.model.PhotoEntity.PhotoContract
import ru.maxim.unsplash.persistence.model.TagEntity.TagContract

@Dao
interface PhotoDao {


    @Query("SELECT * FROM ${PhotoContract.tableName} ORDER BY ${PhotoContract.Columns.cacheTime}")
    fun getAll(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM ${PhotoContract.tableName} WHERE ${PhotoContract.Columns.id}=:id")
    fun getById(id: String): Flow<PhotoEntity?>

    @Query(
        """
        SELECT * FROM ${PhotoContract.tableName} 
        INNER JOIN ${CollectionPhotoContract.tableName}
        ON 
            ${PhotoContract.tableName}.${PhotoContract.Columns.id} = 
            ${CollectionPhotoContract.tableName}.${CollectionPhotoContract.Columns.photoId}
        WHERE ${CollectionPhotoContract.tableName}.${CollectionPhotoContract.Columns.collectionId}=:collectionId
        ORDER BY ${PhotoContract.tableName}.${PhotoContract.Columns.cacheTime}"""
    )
    fun getByCollectionId(collectionId: String): Flow<List<PhotoEntity>>

    @Query(
        """SELECT * FROM ${PhotoContract.tableName}
              INNER JOIN ${TagContract.tableName}
                  ON 
                      ${PhotoContract.tableName}.${PhotoContract.Columns.id} = 
                      ${TagContract.tableName}.${TagContract.Columns.photoId} 
              WHERE ${PhotoContract.tableName}.${PhotoContract.Columns.description} LIKE :query 
              OR ${TagContract.tableName}.${TagContract.Columns.title} LIKE :query
              ORDER BY ${PhotoContract.tableName}.${PhotoContract.Columns.cacheTime}"""
    )
    fun search(query: String): Flow<List<PhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photoEntity: PhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(photoEntities: List<PhotoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCollectionRelation(relation: CollectionPhotoCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCollectionRelations(relations: List<CollectionPhotoCrossRef>)

    @Update
    fun update(photoEntity: PhotoEntity)

    @Delete
    fun delete(photoEntity: PhotoEntity)

    @Query("DELETE FROM ${PhotoContract.tableName}")
    fun deleteAll()
}