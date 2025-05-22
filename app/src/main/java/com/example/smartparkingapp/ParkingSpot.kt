package com.example.smartparkingapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParkingSpot(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val price: Double = 0.0,
    val isAvailable: Boolean = true,
    val ownerId: String = "",
    val totalSpots: Int = 1,
    val availableSpots: Int = 1,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String = ""
) : Parcelable

data class Booking(
    val id: String = "",
    val parkingSpotId: String = "",
    val userId: String = "",
    val startTime: Long = 0,
    val endTime: Long = 0,
    val status: BookingStatus = BookingStatus.PENDING,
    val totalPrice: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}
