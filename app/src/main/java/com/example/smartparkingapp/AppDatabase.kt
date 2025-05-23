package com.example.smartparkingapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ParkingHistoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun parkingHistoryDao(): ParkingHistoryDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_parking_db"
                ).build().also { instance = it }
            }
        }
    }
}
