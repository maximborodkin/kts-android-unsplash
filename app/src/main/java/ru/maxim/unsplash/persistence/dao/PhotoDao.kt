package ru.maxim.unsplash.persistence.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import ru.maxim.unsplash.persistence.model.CollectionPhotoCrossRef
import ru.maxim.unsplash.persistence.model.CollectionPhotoCrossRef.CollectionPhotoContract
import ru.maxim.unsplash.persistence.model.PhotoEntity
import ru.maxim.unsplash.persistence.model.PhotoEntity.PhotoContract
import ru.maxim.unsplash.persistence.model.TagEntity.TagContract
import ru.maxim.unsplash.persistence.model.UserLikeCrossRef
import ru.maxim.unsplash.persistence.model.UserLikeCrossRef.UserLikeContract
import ru.maxim.unsplash.persistence.model.UserPhotoCrossRef
import ru.maxim.unsplash.persistence.model.UserPhotoCrossRef.UserPhotoContract

@Dao
abstract class PhotoDao {

    @Query("SELECT * FROM ${PhotoContract.tableName} ORDER BY ${PhotoContract.Columns.cacheTime}")
    abstract fun getAll(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM ${PhotoContract.tableName} WHERE ${PhotoContract.Columns.id}=:id")
    abstract fun getById(id: String): Flow<PhotoEntity?>

    @Query(
        """
        SELECT * FROM ${PhotoContract.tableName} 
        INNER JOIN ${CollectionPhotoContract.tableName}
        ON 
            ${PhotoContract.tableName}.${PhotoContract.Columns.id}=
            ${CollectionPhotoContract.tableName}.${CollectionPhotoContract.Columns.photoId}
        WHERE ${CollectionPhotoContract.tableName}.${CollectionPhotoContract.Columns.collectionId}=
            :collectionId
        ORDER BY ${PhotoContract.tableName}.${PhotoContract.Columns.cacheTime}"""
    )
    @RewriteQueriesToDropUnusedColumns
    abstract fun getByCollectionId(collectionId: String): Flow<List<PhotoEntity>>

    @Query(
        """
        SELECT * FROM ${PhotoContract.tableName} 
        INNER JOIN ${UserPhotoContract.tableName}
        ON 
            ${PhotoContract.tableName}.${PhotoContract.Columns.id}=
            ${UserPhotoContract.tableName}.${UserPhotoContract.Columns.photoId}
        WHERE ${UserPhotoContract.tableName}.${UserPhotoContract.Columns.userUsername}=:username
        ORDER BY ${PhotoContract.tableName}.${PhotoContract.Columns.cacheTime}"""
    )
    @RewriteQueriesToDropUnusedColumns
    abstract fun getByUser(username: String): Flow<List<PhotoEntity>>

    @Query(
        """
        SELECT * FROM ${PhotoContract.tableName} 
        INNER JOIN ${UserLikeContract.tableName}
        ON
            ${PhotoContract.tableName}.${PhotoContract.Columns.id}=
            ${UserLikeContract.tableName}.${UserLikeContract.Columns.photoId}
        WHERE ${UserLikeContract.tableName}.${UserLikeContract.Columns.userUsername}=:username"""
    )
    @RewriteQueriesToDropUnusedColumns
    abstract fun getLikedByUser(username: String): Flow<List<PhotoEntity>>

    @Query(
        """
        SELECT * FROM ${PhotoContract.tableName}
          INNER JOIN ${TagContract.tableName}
              ON 
                  ${PhotoContract.tableName}.${PhotoContract.Columns.id}=
                  ${TagContract.tableName}.${TagContract.Columns.photoId} 
          WHERE ${PhotoContract.tableName}.${PhotoContract.Columns.description} LIKE :query 
          OR ${TagContract.tableName}.${TagContract.Columns.title} LIKE :query
          ORDER BY ${PhotoContract.tableName}.${PhotoContract.Columns.cacheTime}"""
    )
    @RewriteQueriesToDropUnusedColumns
    abstract fun search(query: String): Flow<List<PhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(photoEntity: PhotoEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(photoEntities: List<PhotoEntity>)

    open suspend fun insertOrUpdate(photoEntity: PhotoEntity) {
        if (getById(photoEntity.id).catch {  }.firstOrNull() != null) {
            update(photoEntity)
        } else {
            insert(photoEntity)
        }
    }

    open suspend fun insertOrUpdate(photoEntities: List<PhotoEntity>) {
        photoEntities.forEach { insertOrUpdate(it) }
    }

    @Transaction
    open suspend fun insertForCollection(photoEntities: List<PhotoEntity>, collectionId: String) {
        insertOrUpdate(photoEntities)
        val relations =
            photoEntities.map { photo -> CollectionPhotoCrossRef(collectionId, photo.id) }
        insertCollectionRelations(relations)
    }

    @Transaction
    open suspend fun insertForUser(photoEntities: List<PhotoEntity>, userUsername: String) {
        insertOrUpdate(photoEntities)
        val relations = photoEntities.map { photo -> UserPhotoCrossRef(userUsername, photo.id) }
        insertUserRelations(relations)
    }

    @Transaction
    open suspend fun insertForLikes(photoEntities: List<PhotoEntity>, userUsername: String) {
        insertOrUpdate(photoEntities)
        val relations = photoEntities.map { photo -> UserLikeCrossRef(userUsername, photo.id) }
        insertLikeRelations(relations)
    }

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertLikeRelations(relations: List<UserLikeCrossRef>)

//    suspend fun insertOrUpdate(photoEntity: PhotoEntity) =
//        if (getById(photoEntity.id).catch { emit(null) }.firstOrNull() != null)
//            update(photoEntity)
//        else
//            insert(photoEntity)
//
//    suspend fun insertOrUpdate(photoEntities: List<PhotoEntity>) =
//        photoEntities.forEach { insertOrUpdate(it) }

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCollectionRelations(relations: List<CollectionPhotoCrossRef>)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    abstract suspend fun insertUserRelation(relation: UserPhotoCrossRef)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUserRelations(relations: List<UserPhotoCrossRef>)

    @Update
    abstract suspend fun update(photoEntity: PhotoEntity)

    @Delete
    abstract suspend fun delete(photoEntity: PhotoEntity)

    @Transaction
    @Query("DELETE FROM ${PhotoContract.tableName}")
    abstract suspend fun deleteAll()
}