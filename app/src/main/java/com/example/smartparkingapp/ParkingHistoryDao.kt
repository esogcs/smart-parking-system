package com.example.smartparkingapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingHistoryDao {
    @Insert
    suspend fun insert(history: ParkingHistoryEntity)

    @Query("SELECT * FROM parking_history WHERE userId = :userId ORDER BY parkedAt DESC")
    fun getHistoryForUser(userId: String): Flow<List<ParkingHistoryEntity>>
}