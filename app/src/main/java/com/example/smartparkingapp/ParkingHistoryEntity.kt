package com.example.smartparkingapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parking_history")
data class ParkingHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val locationName: String,
    val parkedAt: Long,
    val durationMinutes: Int
)