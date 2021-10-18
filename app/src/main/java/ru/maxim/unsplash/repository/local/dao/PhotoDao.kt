package ru.maxim.unsplash.repository.local.dao

import androidx.room.*
import ru.maxim.unsplash.repository.local.model.DatabasePhoto
import ru.maxim.unsplash.repository.local.model.DatabasePhoto.PhotoContract

@Dao
interface PhotoDao {

    @Query("SELECT * FROM ${PhotoContract.tableName}")
    fun getAll(): List<DatabasePhoto>

    @Query("SELECT * FROM ${PhotoContract.tableName} WHERE ${PhotoContract.Columns.id}=:id")
    fun getById(id: String): DatabasePhoto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photo: DatabasePhoto)

    @Update
    fun update(photo: DatabasePhoto)

    @Delete
    fun delete(photo: DatabasePhoto)


//    fun insertPhotoWithRelations(photo: Photo) {
//        insert(photo)
//        Database.instance.userDao().insert(photo.user)
//        photo.tags?.let { tags -> Database.instance.tagDao().insertAll(tags.map { tag -> tag.apply { photoId = photo.id } }) }
//    }
}