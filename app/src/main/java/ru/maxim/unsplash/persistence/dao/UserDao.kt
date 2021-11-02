package ru.maxim.unsplash.persistence.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.maxim.unsplash.persistence.model.UserEntity
import ru.maxim.unsplash.persistence.model.UserEntity.UserContract

@Dao
interface UserDao {

    @Query("SELECT * FROM ${UserContract.tableName}")
    fun getAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM ${UserContract.tableName} WHERE ${UserContract.Columns.id}=:id")
    fun getById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM ${UserContract.tableName} WHERE ${UserContract.Columns.username}=:username")
    fun getByUsername(username: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userEntity: UserEntity)

    @Update(entity = UserEntity::class)
    suspend fun update(userEntity: UserEntity)

    @Delete
    fun delete(userEntity: UserEntity)
}