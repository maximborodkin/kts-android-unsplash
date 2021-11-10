package ru.maxim.unsplash.persistence.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import ru.maxim.unsplash.persistence.model.UserEntity
import ru.maxim.unsplash.persistence.model.UserEntity.UserContract

@Dao
abstract class UserDao {

    @Query("SELECT * FROM ${UserContract.tableName}")
    abstract fun getAll(): Flow<List<UserEntity>>

    @Query(
        "SELECT * FROM ${UserContract.tableName} WHERE ${UserContract.Columns.username}=:username"
    )
    abstract fun getByUsername(username: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(userEntity: UserEntity)

    open suspend fun insertOrUpdate(userEntity: UserEntity) {
        if (getByUsername(userEntity.username).catch {  }.firstOrNull() != null) {
            update(userEntity)
        } else {
            insert(userEntity)
        }
    }

    @Update(entity = UserEntity::class)
    abstract suspend fun update(userEntity: UserEntity)

    @Delete
    abstract suspend fun delete(userEntity: UserEntity)
}