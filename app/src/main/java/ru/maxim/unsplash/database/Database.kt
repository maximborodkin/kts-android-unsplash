package ru.maxim.unsplash.database

import android.content.Context
import androidx.room.Room

object Database {
    lateinit var instance: UnsplashDatabase
        private set

    fun init(context: Context) {
        instance = Room.databaseBuilder(
            context,
            UnsplashDatabase::class.java,
            UnsplashDatabase.databaseName
        ).build()
    }
}