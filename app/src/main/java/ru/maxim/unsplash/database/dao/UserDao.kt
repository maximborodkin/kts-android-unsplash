package ru.maxim.unsplash.database.dao

import androidx.room.*
import ru.maxim.unsplash.database.model.UserEntity
import ru.maxim.unsplash.database.model.UserEntity.UserContract

@Dao
interface UserDao {

    @Query("SELECT * FROM ${UserContract.tableName}")
    fun getAll(): List<UserEntity>

    @Query("SELECT * FROM ${UserContract.tableName} WHERE ${UserContract.Columns.id}=:id")
    fun getById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userEntity: UserEntity)

    @Update
    fun update(userEntity: UserEntity)

    @Delete
    fun delete(userEntity: UserEntity)
}