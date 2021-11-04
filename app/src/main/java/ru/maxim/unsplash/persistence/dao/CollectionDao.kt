package ru.maxim.unsplash.persistence.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.maxim.unsplash.persistence.model.CollectionEntity
import ru.maxim.unsplash.persistence.model.CollectionEntity.CollectionContract
import ru.maxim.unsplash.persistence.model.UserCollectionCrossRef
import ru.maxim.unsplash.persistence.model.UserCollectionCrossRef.UserCollectionContract

@Dao
abstract class CollectionDao {

    @Query(
        """
        SELECT * FROM ${CollectionContract.tableName}
        ORDER BY ${CollectionContract.Columns.cacheTime}"""
    )
    abstract fun getAll(): Flow<List<CollectionEntity>>

    @Query(
        "SELECT * FROM ${CollectionContract.tableName} WHERE ${CollectionContract.Columns.id}=:id"
    )
    abstract fun getById(id: String): Flow<CollectionEntity?>

    @Query(
        """
        SELECT * FROM ${CollectionContract.tableName} 
        INNER JOIN ${UserCollectionContract.tableName}
        ON
            ${CollectionContract.tableName}.${CollectionContract.Columns.id}=
            ${UserCollectionContract.tableName}.${UserCollectionContract.Columns.collectionId}
        WHERE ${UserCollectionContract.tableName}.${UserCollectionContract.Columns.userUsername}
            =:userUsername"""
    )
    @RewriteQueriesToDropUnusedColumns
    abstract fun getByUser(userUsername: String): Flow<List<CollectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(collectionEntity: CollectionEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(collectionEntities: List<CollectionEntity>)

    @Transaction
    open suspend fun insertForUser(collectionEntities: List<CollectionEntity>, userUsername: String) {
        insert(collectionEntities)
        val relations = collectionEntities.map { collection ->
            UserCollectionCrossRef(
                userUsername,
                collection.id
            )
        }
        insertUserCollectionRelations(relations)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUserCollectionRelations(relations: List<UserCollectionCrossRef>)

    @Update(entity = CollectionEntity::class)
    abstract suspend fun update(collectionEntity: CollectionEntity)

    @Delete
    abstract suspend fun delete(collectionEntity: CollectionEntity)

    @Transaction
    @Query("DELETE FROM ${CollectionContract.tableName}")
    abstract suspend fun deleteAll()
}