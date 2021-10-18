package ru.maxim.unsplash.repository.local.dao

import androidx.room.*
import ru.maxim.unsplash.repository.local.model.DatabaseUser
import ru.maxim.unsplash.repository.local.model.DatabaseUser.UserContract

@Dao
interface UserDao {

    @Query("SELECT * FROM ${UserContract.tableName}")
    fun getAll(): List<DatabaseUser>

    @Query("SELECT * FROM ${UserContract.tableName} WHERE ${UserContract.Columns.id}=:id")
    fun getById(id: String): DatabaseUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: DatabaseUser)

    @Update
    fun update(user: DatabaseUser)

    @Delete
    fun delete(user: DatabaseUser)
}