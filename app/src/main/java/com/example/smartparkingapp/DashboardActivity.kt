package com.example.smartparkingapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val parkingRecycler = findViewById<RecyclerView>(R.id.parking_recycler_view)
        parkingRecycler.layoutManager = LinearLayoutManager(this)

        val parkingList = listOf(
            ParkingSpot(
                id = "1",
                name = "Downtown Garage",
                address = "Main Street",
                price = 5.00,
                isAvailable = true,
                ownerId = "owner1"
            ),
            ParkingSpot(
                id = "2",
                name = "Mall Lot",
                address = "5th Avenue",
                price = 3.50,
                isAvailable = true,
                ownerId = "owner2"
            ),
            ParkingSpot(
                id = "3",
                name = "Airport Parking",
                address = "Airport Blvd",
                price = 7.25,
                isAvailable = true,
                ownerId = "owner3"
            )
        )

        // Set up the adapter with the parking list
        val adapter = ParkingSpotAdapter(parkingList) { spot ->
            // Show a toast message when a parking spot is clicked
            Toast.makeText(this, "Selected: ${spot.name}", Toast.LENGTH_SHORT).show()
        }
        parkingRecycler.adapter = adapter
    }
}
