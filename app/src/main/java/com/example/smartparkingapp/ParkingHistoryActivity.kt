package com.example.smartparkingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Date

class ParkingHistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // For example purposes, create a dummy list:
        val sampleHistory = listOf(
            ParkingHistoryEntity(
                userId = "user1",
                locationName = "Downtown Garage",
                parkedAt = 1684800000000,
                durationMinutes = 60
            ),
            ParkingHistoryEntity(
                userId = "user2",
                locationName = "Airport Parking",
                parkedAt = 1684700000000,
                durationMinutes = 120
            )
        )


        setContent {
            ParkingHistoryScreen(parkingHistoryList = sampleHistory)
        }
    }
}
