package com.example.smartparkingapp

import android.os.Bundle
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
            ParkingSpot("Downtown Garage", "Main Street", 5.00),
            ParkingSpot("Mall Lot", "5th Avenue", 3.50),
            ParkingSpot("Airport Parking", "Airport Blvd", 7.25)
        )

        val adapter = ParkingAdapter(parkingList)
        parkingRecycler.adapter = adapter
    }
}
