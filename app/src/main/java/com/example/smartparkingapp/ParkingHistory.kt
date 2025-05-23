package com.example.smartparkingapp

import java.util.UUID

data class ParkingHistory(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val locationName: String,
    val parkedAt: Long, // timestamp
    val durationMinutes: Int
)
