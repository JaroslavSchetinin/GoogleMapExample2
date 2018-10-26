package com.example.admin.googlemapexample.db

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.content.Context
import com.example.admin.googlemapexample.model.Station


@Database(entities = [Station::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun stationDao(): StationsDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "mydb").build()
            }
            return instance
        }
    }
}